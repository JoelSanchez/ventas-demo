(ns ventas-demo.core
  (:gen-class)
  (:refer-clojure :exclude [reset!])
  (:require
   [me.raynes.fs :as f]
   [clojure.tools.logging :as log]
   [mount.core :as mount]
   [ventas-demo.handler :as handler]
   [ventas.config :as config]
   [ventas.core :as ventas]
   [ventas.plugins.stripe.core]
   [ventas.plugins.slider.core]
   [ventas.search.indexing :as search.indexing]
   [ventas.server :as server]
   [ventas.system :as system]
   [ventas.themes.clothing.core]
   [ventas.themes.clothing.demo :as clothing-theme.demo]
   [ventas.entities.category :as category]
   [ventas.plugins.stripe.api :as stripe.api]
   [ventas.entities.product :as product]
   [ventas.database.entity :as entity]
   [ventas.storage :as storage]
   [ventas.utils.jar :as utils.jar]
   [clojure.string :as str]
   [ventas.entities.file :as entities.file])
  (:import [java.util.jar JarFile JarEntry]))

(defn- set-featured-entities! []
  (doseq [entity (take 5 (product/products-with-images))]
    (entity/update* {:db/id (:db/id entity)
                     :product/featured true}))
  (doseq [entity (take 5 (category/categories-with-products))]
    (entity/update* {:db/id (:db/id entity)
                     :category/featured true})))

(defn- configure-stripe! []
  (let [{:keys [private-key public-key]} (config/get :stripe)]
    (when (and private-key public-key)
      (log/info "Configuring stripe plugin from the static configuration")
      (stripe.api/set-config! {:stripe-plugin/private-key private-key
                               :stripe-plugin/public-key public-key}))))

(defn- extract-dir-from-jar-to-storage
  "Takes the string path of a jar, a dir name inside that jar and a destination
  dir, and copies the from dir to the to dir."
  [^String jar-dir from]
  (let [jar (JarFile. jar-dir)]
    (doseq [^JarEntry file (enumeration-seq (.entries jar))]
      (when (and (.startsWith (.getName file) from)
                 (not (.isDirectory file)))
        (let [filename (last (str/split (.getName file) #"\/"))]
          (with-open [is (.getInputStream jar file)]
            (storage/put-object filename is)))))))

(defn copy-demo-images! []
  (extract-dir-from-jar-to-storage utils.jar/running-jar "ventas_demo/demo_images"))

(defn- reset!
  "Returns everything to its default state, removing all data"
  []
  (ventas/setup! :recreate? true)
  (clothing-theme.demo/install-demo-data!)
  (search.indexing/reindex)
  (set-featured-entities!)
  (configure-stripe!)
  (copy-demo-images!)
  (entities.file/clean-storage)
  (entities.file/transform-all))

(defn start! []
  (-> (mount/only system/default-states)
      (mount/with-args {::server/handler #'handler/handler})
      mount/start)
  :done)

(defn -main [& args]
  (mount/start #'storage/storage-backend)
  (copy-demo-images!)
  (start!)
  (when (config/get :reset-on-restart)
    (log/info ":reset-on-restart is on -- removing all data")
    (reset!)))


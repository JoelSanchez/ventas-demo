(ns ventas-demo.handler
  (:require
   [ventas.themes.clothing.core :as clothing-theme]
   [ventas.plugins.stripe.core :as stripe-plugin]
   [ventas.plugins.slider.core :as slider-plugin]
   [ventas.html :as html]
   [ventas.server.admin-spa :as admin-spa]
   [compojure.core :as compojure]
   [ventas.server :as server]))

(defn middleware [handler]
  (fn [req]
    (handler
     (-> req
         (html/enqueue-js ::clothing-theme/base "/files/js/app/main.js")))))

(def handler
  (compojure/routes
   server/http-handler
   (-> clothing-theme/routes
       (middleware)
       (admin-spa/css-middleware)
       (clothing-theme/middleware)
       (stripe-plugin/middleware)
       (slider-plugin/middleware))))
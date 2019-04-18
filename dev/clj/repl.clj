(ns repl
  (:require
    [clojure.tools.namespace.repl :as tn]
    [mount.core :as mount]
    [ventas.system :as system]
    [shadow.cljs.devtools.server :as shadow.server]
    [shadow.cljs.devtools.api :as shadow.api]))

(defn refresh [& {:keys [after]}]
  (let [result (tn/refresh :after after)]
    (when (instance? Throwable result)
      (throw result))))

(defn refresh-all [& {:keys [after]}]
  (let [result (tn/refresh-all :after after)]
    (when (instance? Throwable result)
      (throw result))))

(alter-var-root #'*warn-on-reflection* (constantly true))
(tn/set-refresh-dirs)

(defn r [& subsystems]
  (let [states (system/get-states subsystems)]
    (when (seq states)
      (mount/stop states))
    (refresh :after 'ventas-demo.core/start!)))

(defn init []
  (require 'ventas.core)
  (refresh-all :after 'ventas-demo.core/start!))

(defn watch-cljs [build-id]
  (shadow.server/start!)
  (shadow.api/watch build-id))

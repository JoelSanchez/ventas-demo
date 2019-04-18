(comment
  [com.datomic/datomic-pro "0.9.5697" :exclusions [org.slf4j/slf4j-nop org.slf4j/slf4j-log4j12
                                                   org.fressian/fressian
                                                   commons-codec
                                                   org.apache.httpcomponents/httpclient
                                                   org.slf4j/slf4j-api]]
  [com.datastax.cassandra/cassandra-driver-core "3.3.1" :exclusions [org.slf4j/slf4j-api]])

(defproject ventas-demo "0.0.1"
  :description "Demo distribution of ventas"

  :pedantic? :abort

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.9.0" :scope "provided" :exclusions [org.clojure/spec.alpha]]
                 [org.clojure/tools.namespace "0.3.0-alpha4"]
                 [io.sentry/sentry-logback "1.7.5" :exclusions [org.slf4j/slf4j-api
                                                                ch.qos.logback/logback-core
                                                                ch.qos.logback/logback-classic
                                                                com.fasterxml.jackson.core/jackson-core]]
                 [io.logz.logback/logzio-logback-appender "1.0.20" :exclusions [org.slf4j/slf4j-api]]
                  ;; transitive
                 [org.clojure/tools.reader "1.3.0-alpha3"]
                 [com.google.guava/guava "21.0"]

                 [ventas-clothing-theme "0.1.3" :exclusions [ventas-core]]
                 [ventas-stripe-plugin "0.1.1" :exclusions [ventas-core]]
                 [ventas-slider-plugin "0.1.1" :exclusions [ventas-core]]
                 [ventas-core "0.1.1"]]

  :repositories {"my.datomic.com"
                 ~(merge
                    {:url "https://my.datomic.com/repo"}
                    (let [username (System/getenv "DATOMIC__USERNAME")
                          password (System/getenv "DATOMIC__PASSWORD")]
                      (when (and username password)
                        {:username username
                         :password password})))}

  :source-paths ["src/clj" "src/cljs" "src/cljc"]

  :jvm-opts ["-XX:-OmitStackTraceInFastThrow"
             "-Dapple.awt.UIElement=true" ;; Disable empty/useless menu item in OSX
             ]

  :clean-targets ^{:protect false} [:target-path
                                    :compile-path
                                    "storage/rendered"
                                    "resources/public/files/js/admin/cljs-runtime"
                                    "resources/public/files/js/app/cljs-runtime"]

  :uberjar-name "ventas.jar"

  :main ventas-demo.core

  :target-path "target/%s"

  :profiles {:dev {:repl-options {:init-ns repl
                                  :nrepl-middleware [shadow.cljs.devtools.server.nrepl04/cljs-load-file
                                                     shadow.cljs.devtools.server.nrepl04/cljs-eval
                                                     shadow.cljs.devtools.server.nrepl04/cljs-select]
                                  :timeout 120000}
                   :dependencies [[cider/piggieback "0.3.9" :exclusions [org.clojure/clojurescript org.clojure/tools.logging]]
                                  [binaryage/devtools "0.9.10"]
                                  [thheller/shadow-cljs "2.7.21" :exclusions [org.clojure/tools.reader
                                                                              com.google.guava/guava
                                                                              org.clojure/tools.cli
                                                                              commons-codec
                                                                              commons-io
                                                                              ring/ring-core
                                                                              nrepl]]
                                  [org.clojure/tools.namespace "0.3.0-alpha4"]]
                   :source-paths ["dev/clj" "dev/cljs"]}
             :uberjar {:source-paths ^:replace ["src/clj" "src/cljc"]
                       :omit-source true
                       :aot [clojure.tools.logging.impl ventas.core ventas-demo.core]}})

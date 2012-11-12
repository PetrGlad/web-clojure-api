(defproject cljdb "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/data.json "0.1.3"]
                 
                 [org.slf4j/slf4j-api "1.6.6"]
                 [org.slf4j/log4j-over-slf4j "1.6.6"]
                 [ch.qos.logback/logback-classic "1.0.6"]

                 [korma "0.3.0-beta9"]
                 [postgresql "9.0-801.jdbc4"]

                 [noir "1.3.0-beta3"]
                 [com.cemerick/friend "0.1.2"]

                 ; TODO Configure DB migrations https://github.com/macourtney/drift
                 [drift "1.4.2"]
                 
                 [hiccup "1.0.1"]
                 [commons-codec/commons-codec "1.6"]]
  :main cljdb.server)

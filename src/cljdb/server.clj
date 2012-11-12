(ns cljdb.server
  (:require [noir.server :as server]            
            [cemerick.friend :as friend]
            [cemerick.friend [workflows :as workflows]
                             [credentials :as creds]]
            [cljdb.auth]))

(server/load-views-ns 'cljdb.views)

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8085"))]    
    (server/add-middleware 
      friend/authenticate
      {:credential-fn cljdb.auth/authenticate 
       :workflows [(workflows/interactive-form)]})    
    ; (server/gen-handler {:mode mode :ns 'cljdb})
    (server/start port {:mode mode
                        :ns 'cljdb})))

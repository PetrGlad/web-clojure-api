(ns cljdb.auth  
  (require [cemerick.friend.credentials :as creds])
  (import java.security.MessageDigest
          ;java.security.NoSuchAlgorithmException
          org.apache.commons.codec.binary.Base64)
  (use korma.core
       [cljdb.core :only [users credentials roles users_to_roles]]))

(def salt "Some salt here")

(defn hash-password [^String plainTextPassword]
  (Base64/encodeBase64String 
    (let [md (MessageDigest/getInstance "SHA-256")]
      (.update md (.getBytes salt))
      (.digest md (.getBytes plainTextPassword)))))

(defn get-credentials [userName password]  
  (first (select credentials 
          (where (and 
                   (= :name userName)
                   (= :password (hash-password password)))))))

(defn get-roles [userId]
  (into #{}
        (map #(keyword (str (ns-name *ns*)) (:name %)) 
          (select users_to_roles 
                  (join roles (= :users_to_roles.role_id :roles.id))
                  (fields :roles.name)))))

(defn authenticate [{:keys [username password]}] 
  (if-let [creds (get-credentials username password)]
    (let [userId (:user_id creds)
          roles (get-roles userId)]
      {:user_id userId
       :username (:name creds)
       :roles #{::admin}})))

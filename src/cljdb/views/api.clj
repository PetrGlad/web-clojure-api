(ns cljdb.views.api 
  (:require [cljdb.views.common :as common]
            [cemerick.friend :as friend]
            [noir.content.getting-started]
            [noir.session :as web-session]
            [ring.util.response]
            [noir.response :as response]
            [clojure.java.io]
            [noir.request])
  (:use [noir.core :only [defpage]]
        [cljdb.core]
        [korma.core]
        [clojure.data.json :only [read-json json-str]]))

; TODO Multimethod entity->json 
; TODO DRY data mapping function

(defpage list-files "/files" {}
  (response/json
    (map #(update-in % [:created] str)
         (select file_descriptors))))

(defpage upload-file [:post "/files"] {file :file}
  ; Uploaded file ["size", "tempfile", "content-type", "filename"]  
  (friend/authenticated (do ; see issue https://github.com/cemerick/friend/issues/32 
       (clojure.java.io/copy (:tempfile file) (java.io.File. "/tmp/" (:filename file)))
       (let [newCourse (insert file_descriptors
                               (values {:file_name (:filename file),
                                        :type_tag (:content-type file),
                                        :state "N",
                                        :description "",
                                        :owner_id (:user_id (friend/current-authentication)),
                                        :created (java.sql.Timestamp. (System/currentTimeMillis))}))]
         (response/json newCourse)))))


(defpage get-file "/files/:fileId" {fileId :fileId}
  ; TODO DRY queries (and move to data-access-package, maybe)
  (response/json
    (first (map #(update-in % [:created] str)
                (select file_descriptors (where (= :id (Long/parseLong fileId))))))))

(defpage create-course [:post "/courses"] {:keys [courseTitle]}
  ; Create new course
  (friend/authenticated
    (let [newCourse (insert courses
                            (values {:title courseTitle,                                  
                                     :owner_id (:user_id (friend/current-authentication)),
                                     :created (java.sql.Timestamp. (System/currentTimeMillis)),
                                     :active true}))]
      (response/json newCourse))))

(defpage list-courses "/courses" {}
  (response/json (map #(update-in % [:created] str)
                        (select courses))))

(defpage "/courses/my" {}
  (response/json
    (map #(update-in % [:created] str)
         (select courses (where (= :owner_id (:user_id (friend/current-authentication))))))))

(defpage "/courses/:courseId" {courseId :courseId}  
  (response/json
    (map #(update-in % [:created] str)
         (select courses (where (= :id (Long/parseLong courseId)))))))

(defpage "/lessons" {}
  (response/json
    (select lessons)))

(defpage "/lessons/:lessonId" {lessonId :lessonId}  
  (response/json 
    (first (select lessons (where (= :id (Long/parseLong lessonId)))))))

(defpage "/users" {}
  (friend/authorize 
    #{:cljdb.auth/admin} {}
    (response/json 
      (select users
              (join users_to_roles (= :users_to_roles.user_id :users.id))
              (join roles (= :users_to_roles.role_id :roles.id))
              (fields :users.id :users.first_name :users_to_roles.role_id)))))

(defpage "/users/:userId" {userId :userId}
  (friend/authorize 
    #{:cljdb.auth/admin} {}
    (response/json 
      (select users 
              (where :user_id)
              (join users_to_roles (= :users_to_roles.user_id :users.id))
              (join roles (= :users_to_roles.role_id :roles.id))
              (fields :users.id :users.first_name :users_to_roles.role_id)))))


; ---------------------------------------------------------
; HTML pages

(defpage "/filesList" {}
  ; Hiccup HTML generation demo
  (common/layout
    [:div 
     [:h1 "File names list"] 
     [:ul (map #(vector :li (:file_name %)) 
               (select file_descriptors (fields :file_name)))]]))

(defpage create-course-ui "/ui/createCourse" {}
  (common/layout [:form {:action "/courses" :method "post"} 
                  [:label "New course title" [:input {:name "courseTitle" :type "text"}]]                  
                  [:input {:type "submit" :name "submit"}]]))

(defpage upload-file-ui "/ui/uploadFile" {}  
  (common/layout [:form {:action "/files" :method "post" :enctype "multipart/form-data"} 
                    [:label "File to upload" [:input {:name "file" :type "file"}]]
                    [:input {:type "submit" :name "submit"}]]))

(defpage "/login" {}
  (common/layout [:form {:action "/login" :method "post"} 
                  [:label "Username" [:input {:name "username" :type "text"}]]
                  [:label "Password" [:input {:name "password" :type "password"}]]
                  [:input {:type "submit" :name "submit"}]]))

(defpage "/logout" {}
  (web-session/clear!)
  (ring.util.response/redirect "/"))


; ---------------------------------------------------------
; Debugging functions

(defpage "/session" {} 
  {:content-type "application/json"
   :body (json-str @web-session/*noir-session*)})

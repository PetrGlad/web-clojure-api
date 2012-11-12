(ns cljdb.core
  (:use korma.core)
  (:use korma.db))

(defdb db (postgres {:db "dbname"
                     :user "dbuser"
                     :password "dbpassword"))

(defentity users)

(defentity credentials 
  (belongs-to users {:fk :user_id}))

(defentity courses)

(defentity lessons  
  (belongs-to courses {:fk :course_id}))

(defentity topics  
  (belongs-to lessons {:fk :lesson_id}))

(defentity file_descriptors)

(defentity roles)

(defentity permissions)

(defentity roles_to_permissions
  (has-one roles {:fk :role_id})
  (has-one permissions {:fk :permission_id}))

(defentity users_to_roles  
  (has-one users {:fk :user_id})
  (has-one roles {:fk :role_id}))

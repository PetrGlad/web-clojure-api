(ns cljdb.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-css html5]]))

(defpartial layout [& content]
  (html5
    [:head
     [:title "Web app in clojure"]
     (include-css "/css/main.css")]
    [:body
     [:div#wrapper
      content]]))

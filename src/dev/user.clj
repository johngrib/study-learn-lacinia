(ns user
  (:require [clojure.data.json :as json]
            [clojure.pprint :refer [pprint]]))

(def books (json/read-str (slurp "resources/data/books.json")))

(comment
  (pprint (first books)))

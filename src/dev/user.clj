(ns user
  (:require [clojure.data.json :as json]
            [clojure.pprint :refer [pprint]]
            [clojure.edn :as edn]
            [com.walmartlabs.lacinia :refer [execute]]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [com.walmartlabs.lacinia.schema :as schema]
            [got.schema :refer [load-schema]]))

(def books (-> (slurp "resources/data/books.json")
               (json/read-str :key-fn keyword)
               #_(index-by :Id)))

(def characters (-> (slurp "resources/data/characters.json")
                    (json/read-str :key-fn keyword)
                    count))

(def houses (-> (slurp "resources/data/houses.json")
                (json/read-str :key-fn keyword)
                count))

(def got-schema (load-schema))

(defn q
  [query-string]
  (execute got-schema query-string nil nil))

(comment
  (pprint books)
  (pprint (first books))
  (count (first books))

  (schema/compile (-> (slurp "resources/schema.edn")
                      edn/read-string
                      (attach-resolvers {})))

  (load-schema)

  (q "{ book_by_id(id: 11) { Id Name }}") ;; {:data {:book_by_id {:Id 11, :Name "The World of Ice and Fire"}}}
  )



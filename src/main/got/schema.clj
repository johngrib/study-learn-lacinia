(ns got.schema
  (:require [clojure.data.json :as json]
            [clojure.pprint :refer [pprint]]
            [clojure.edn :as edn]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia :refer [execute]]))

;; helper functions
(defn index-by
  [coll key-fn]
  (into {}
        (map (juxt key-fn identity) coll)))

(def books (-> (slurp "resources/data/books.json")
               (json/read-str :key-fn keyword)
               (index-by :Id)))

(comment
  (pprint books)
  (pprint (first books))
  (count (first books)))

;; resolvers
(defn get-book
  "book 에 대한 graphql resolver"
  [context {:keys [id]} value]
  (get books id))

(defn resolver-map
  "resolver 모음"
  []
  {:query/book-by-id get-book})

;; schema
(defn load-schema
  "schema.edn 파일을 읽고 컴파일합니다."
  []
  (-> "resources/schema.edn"
      slurp
      edn/read-string
      (attach-resolvers (resolver-map))
      schema/compile))

(comment
  (load-schema))

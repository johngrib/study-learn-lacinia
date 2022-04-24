(ns got.schema
  (:require [clojure.data.json :as json]
            [clojure.edn :as edn]
            [clojure.pprint :refer [pprint]]
            [com.walmartlabs.lacinia :refer [execute]]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]))

;; helper functions
(defn index-by
  [coll key-fn]
  (into {}
        (map (juxt key-fn identity) coll)))

(def books (-> (slurp "resources/data/books.json")
               (json/read-str :key-fn keyword)
               (index-by :Id)))

(def houses (-> (slurp "resources/data/houses.json")
                (json/read-str :key-fn keyword)
                (index-by :Id)))

(def characters (-> (slurp "resources/data/characters.json")
                    (json/read-str :key-fn keyword)
                    (index-by :Id)))

;; resolvers
(defn get-book
  "book 에 대한 graphql resolver"
  [context {:keys [id]} value]
  (get books id))

(defn get-character
  "등장인물 정보를 리턴합니다."
  [context {:keys [id]} value]
  (get characters id))

(defn get-house
  [id]
  (get houses id))

(defn get-allegiances
  [context args {:keys [Allegiances]}]
  (map get-house Allegiances))

(defn get-books
  [context args {:keys [Books]}]
  (map #(get books %) Books))

(defn resolver-map
  "resolver 모음"
  []
  {:query/book-by-id      get-book
   :query/character-by-id get-character
   :Character/Allegiances get-allegiances
   :Character/Books       get-books})

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
  (pprint books)
  (pprint (first books))
  (count (first books))

  (pprint houses)
  (pprint (first houses))
  (count (first houses))

  (pprint characters)
  (pprint (first characters))
  (count (first characters))

  (load-schema))

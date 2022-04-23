(ns got.server
  (:require [got.schema :as schema]
            [ring.adapter.jetty :as jetty]
            [ring.util.request :as request]
            [ring.middleware.resource :as resource]
            [ring.middleware.content-type :as content-type]
            [ring.middleware.not-modified :as not-modified]
            [reitit.ring :as ring]
            [clojure.data.json :as json]
            [com.walmartlabs.lacinia :as lacinia]))

(def got-schema (schema/load-schema))

(defn graphql-handler
  [request]
  (let [graphql-request (json/read-str (request/body-string request)
                                       :key-fn
                                       keyword)
        {:keys [query variables]} graphql-request
        ; got-schema 에 대해 query 를 실행한 결과
        result (lacinia/execute got-schema query variables nil)]
    {:status  200
     :body    (json/write-str result)
     :headers {"Content-Type" "application/json"}}))

(def app
  "POST /graphql 로 요청이 들어오면 graphql-handler 함수로 응답한다"
  (-> (ring/ring-handler (ring/router ["/graphql" {:post graphql-handler}]))
      (resource/wrap-resource "static")
      content-type/wrap-content-type
      not-modified/wrap-not-modified))

(defonce server (atom nil))

(defn start-server
  []
  (reset! server (jetty/run-jetty app {:join? false
                                       :port 8080})))

(defn stop-server
  []
  (when @server
    (.stop @server)))

(comment
  (start-server)
  (stop-server))

(ns dojo
  (:require [clj-http.client :as http]
            [twitter :as twitter]
            [oauth.client :as oauth]))

(def events (:body (http/get "https://api.github.com/events" {:as :json})))

(def oauth-consumer
  (oauth/make-consumer "<consumer_token>"
    "<consumer_secret>"
    "https://api.twitter.com/oauth/request_token"
    "https://api.twitter.com/oauth/access_token"
    "https://api.twitter.com/oauth/authorize"
    :hmac-sha1 ))

(def oauth-access-token
  "<access_token>")

(def oauth-access-token-secret
  "<access_token_secret>")

(defn push-events [events]
  (filter (fn [event] (= "PushEvent" (:type event)))
    events))

(defn commit-messages [events]
  (map (fn [event] (get-in event [:payload :commits 0 :message ]))
    (push-events events)))

(defn tweet-status [status]
  (println "Tweeting" status)
  (twitter/with-https
    (twitter/with-oauth
      oauth-consumer
      oauth-access-token
      oauth-access-token-secret
      (twitter/update-status status))))

(defn render-event [push-event]
  (let [url (str " in https://github.com/" (get-in push-event [:repo :name ]))]
    (str (apply str
           (take (- 140 (.length url))
             (str
               (get-in push-event [:actor :login ])
               " just did "
               (get-in push-event [:payload :commits 0 :message ]))))
      url)))

(defn -main [& m]
  (let [push-event (first (push-events events))]
    (tweet-status (render-event push-event))))
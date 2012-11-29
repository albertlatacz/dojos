(ns dojo)

(defn say-hello [message]
  (str "Hello " message "!"))

(defn -main [& m]
  (println (say-hello "world")))
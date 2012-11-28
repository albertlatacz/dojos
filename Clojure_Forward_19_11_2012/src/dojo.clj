(ns dojo
  (:use [clojure.tools.namespace]
        [clojure.java.io :only [file]]
        [overtone.live]))

; from https://github.com/ctford/whelmed/blob/master/src/whelmed/instrument.clj
(definst bell [frequency 440 duration 1000
               h0 1 h1 0.6 h2 0.4 h3 0.25 h4 0.2 h5 0.15]
  (let [harmonics [1 2 3 4.2 5.4 6.8]
        proportions [h0 h1 h2 h3 h4 h5]
        proportional-partial
        (fn [harmonic proportion]
          (let [envelope
                (env-gen (perc 0.01 (* proportion (/ duration 1000))))
                overtone
                (* harmonic frequency)]
            (* 1/2 proportion envelope (sin-osc overtone))))
        partials
        (map proportional-partial harmonics proportions)
        whole (mix partials)]
    (detect-silence whole :action FREE)
    whole))

(defn example-var-arity-fn
  ([x] x)
  ([x y] x)
  ([x y z] x))

(defn fns-in-ns [ns]
  (map
    #(ns-resolve ns %)
    (keys (ns-publics ns))))

(defn fn->noise [func]
  (let [fn-name (name (:name (meta func)))
        fn-args (:arglists (meta func))]
    (map
      #(-> {}
         (assoc :frequency (+ 100 (mod (.hashCode fn-name) 600)))
         (assoc :duration (* (count %) 300)))
      fn-args)))

(defn ns->noise [ns]
  (require ns)
  (mapcat fn->noise (fns-in-ns ns)))

(defn clj->noise [dir]
  (mapcat ns->noise (find-namespaces-in-dir (file dir))))

(defn play-note [note]
  (bell note)
  (Thread/sleep (:duration note)))

(defn code-noise [dir]
  (while true
    (doseq
      [note (clj->noise dir)]
      (play-note note))))

(defn -main [& m]
  (code-noise "."))
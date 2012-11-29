(ns dojo_test
  (:use clojure.test
        dojo))

(deftest should-say-hello-world
  (is (= "Hello world!" (say-hello "world"))))
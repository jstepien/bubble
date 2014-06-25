(ns leiningen.bubble-test
  (:require [leiningen.bubble :refer [bubble]]
            [clojure.test :refer :all]
            [clojure.tools.nrepl.server :as nrepl-srv]))

(def ^:private ^:dynamic *nrepl-port* nil)

(def blow nil)

(use-fixtures
  :each
  (fn [f]
    (try
      (eval '[(ns bubble.core)])
      (eval '[(ns bubble.core)
              (defmacro blow
                [& args]
                `(leiningen.bubble-test/blow '~args))])
      (finally
        (in-ns 'leiningen.bubble-test)))
    (let [srv (nrepl-srv/start-server)]
      (try
        (binding [*nrepl-port* (:port srv)]
          (f))
        (finally
          (remove-ns 'bubble.core)
          (nrepl-srv/stop-server srv))))))

(deftest blow-t
  (let [blown (atom nil)]
    (with-redefs [blow (partial reset! blown)]
      (testing "simple working case"
        (bubble {} "blow" "0" (str *nrepl-port*) "bubble-sym" "project.clj")
        (is (= (list 'bubble-sym
                     (cons 'quote
                           [[(read-string
                               (str \[ (slurp "project.clj") \]))]]))
               @blown)))
      (testing "after and before with symbol"
        (bubble {} "blow" "0" (str *nrepl-port*) "bubble-sym"
                ":after" "after-symbol" ":before" "before-symbol" "project.clj")
        (is (= (list 'bubble-sym
                     (cons 'quote
                           [[(read-string
                               (str \[ (slurp "project.clj") \]))]])
                     :before
                     (list `fn '[through] '((through 'before-symbol)))
                     :after
                     (list `fn '[through] '((through 'after-symbol))))
               @blown))))))

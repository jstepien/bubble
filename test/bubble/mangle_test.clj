(ns bubble.mangle-test
  (:require [clojure.test :refer :all]
            [bubble.mangle :refer :all]))

(deftest t-mangle
  (is (= '[[(ns some.core.foo)
            (def x 5)
            (def y some.core.foo/x)]
           [(ns some.other.foo)
            (def z some.core.foo/x)]]
         (mangle '[[(ns some.core)
                    (def x 5)
                    (def y some.core/x)]
                   [(ns some.other)
                    (def z some.core/x)]]
                 :postfix 'foo))))

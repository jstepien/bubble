(ns bubble.core-test
  (:refer-clojure :exclude [pop])
  (:require [clojure.test :refer :all]
            [bubble.core :refer :all]))

(deftest t-blow-bubbles
  (testing "creates ns accessible with bubble/through"
    (let [bubble (init)]
      (try
        (blow bubble
              '[[(ns bubble.t)
                 (def works :yes)]])
        (is (= :yes
               (some-> (through bubble 'bubble.t/works)
                       deref)))
        (finally
          (pop bubble)))))
  (testing "cleans up"
    (let [bubble (init)
          count-all (comp count all-ns)
          before (count-all)]
      (blow bubble '[[(ns bubble.t)]])
      (is (= (inc before) (count-all)))
      (pop bubble)
      (is (= before (count-all)))))
  (testing "through doesn't work after pop"
    (let [bubble (init)]
      (blow bubble '[[(ns bubble.t)
                      (def x 1)]])
      (is (= 1 (some-> (through bubble 'bubble.t/x)
                       deref)))
      (pop bubble)
      (is (nil? (through bubble 'bubble.t/x))))))

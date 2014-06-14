(ns bubble.core-test
  (:refer-clojure :exclude [pop])
  (:require [clojure.test :refer :all]
            [clojure.set :as set]
            [bubble.core :refer :all]))

(deftest t-blow-once
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

(deftest t-blow-more
  (let [blow-once (fn [bubble value & args]
                    (apply blow
                           bubble
                           [['(ns bubble.t)
                             (list 'def 'x value)]]
                           args))]
    (testing "remove old ns"
      (let [bubble (init)
            all (comp set all-ns)
            before (all)
            first-diff (atom ())]
        (try
          (blow-once bubble :x)
          (reset! first-diff (set/difference (all) before))
          (blow-once bubble :y)
          (let [second-diff (set/difference (all) before)]
            (is (= 1 (count second-diff)))
            (is (not= @first-diff second-diff)))
          (finally
            (pop bubble)))))
    (testing "call before"
      (let [bubble (init)
            check (atom [])
            before-fn (fn [new-through]
                        (swap! check
                               conj
                               [(some-> (through bubble 'bubble.t/x) deref)
                                (some-> (new-through 'bubble.t/x) deref)]))]
        (try
          (blow-once bubble :x :before before-fn)
          (blow-once bubble :y :before before-fn)
          (is (= [[nil :x] [:x :y]] @check))
          (finally
            (pop bubble)))))
    (testing "throw in before"
      (let [bubble (init)
            count-all (comp count all-ns)
            before (count-all)]
        (try
          (blow-once bubble :x)
          (is (= :x (deref (through bubble 'bubble.t/x))))
          (is (= (inc before) (count-all)))
          (is (thrown? Exception
                       (blow-once bubble :y :before (fn [_]
                                                      (throw (Exception.))))))
          (is (= :x (deref (through bubble 'bubble.t/x))))
          (is (= (inc before) (count-all)))
          (finally
            (pop bubble)))))
    (testing "call after"
      (let [bubble (init)
            check (atom [])
            after-fn (fn [old-through]
                       (swap! check
                              conj
                              [(some-> (through bubble 'bubble.t/x) deref)
                               (some-> (old-through 'bubble.t/x) deref)]))]
        (try
          (blow-once bubble :x :after after-fn)
          (is (= [] @check))
          (blow-once bubble :y :after after-fn)
          (is (= [[:y :x]] @check))
          (finally
            (pop bubble)))))
    (testing "throwing in after doesn't matter"
      (let [bubble (init)
            after-fn (fn [_]
                       (throw (Exception. "Shouldn't matter!")))]
        (try
          (blow-once bubble :x :after after-fn)
          (blow-once bubble :y)
          (is (= :y @(through bubble 'bubble.t/x)))
          (finally
            (pop bubble)))))
    (testing "keep old nss if new code fails to compile"
      (let [bubble (init)
            all (comp set all-ns)
            before (all)
            first-diff (atom ())]
        (try
          (blow-once bubble :x)
          (reset! first-diff (set/difference (all) before))
          (is (thrown? clojure.lang.Compiler$CompilerException
                       (blow-once bubble 'no-such-symbol)))
          (is (= @first-diff (set/difference (all) before)))
          (is (= :x @(through bubble 'bubble.t/x)))
          (finally
            (pop bubble)))))))

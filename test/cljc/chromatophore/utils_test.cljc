(ns chromatophore.utils-test
  "Tests to verify that utility functions are working properly"
  (:require
   #?@(:cljs [[cljsjs.react.dom.server]
              [cljs.test :refer-macros [testing is are use-fixtures]]
              [devcards.core :refer-macros [deftest]]
              [reagent.core :as reagent]
              [reagent.ratom :refer-macros [reaction]]])
   #?(:clj [clojure.test :refer [deftest testing is are run-tests use-fixtures]])
   [chromatophore.utils
    :refer [atom? #?@(:clj [fnp defnp])]
    #?@(:cljs [:refer-macros [fnp defnp]])]))

(deftest atom?-basic-test
  (testing "The atom? predicate handles ordinary atoms properly"
    (is (= (atom? (atom {})) true))))

#?(:cljs
   (deftest atom?-ratom-test
     (testing "The atom? predicate handles reagent's 'ratoms'"
       (is (= (atom? (reagent/atom {})) true)))))

#?(:cljs
   (deftest atom?-reaction-test
     (testing "The atom? predicate handles reagent's 'reactions'"
       (let [a (reagent/atom {:a "foo"})
             b (reaction {:b (:a @a)})]
         (is (= (atom? a) true))
         (is (= (atom? b) true))))))

(deftest atom?-sad-paths
  (testing "The atom? predicate isn't true of strings, numbers, etc"
    (are [x] (= (atom? x) false)
      "Hello"
      1
      2.0
      #(+ 1 %)
      #"regex"
      #?(:cljs (js/Object.))
      nil
      :foo
      {:foo :bar}
      #{1 2 3}
      [1 2 3]
      true
      false)))

(deftest fnp-tests
  (testing "fnp can make a basic function"
    (let [foo (fnp [_] :bar)]
      (is (= :bar (foo)))))
  (testing "fnp puts in {} for its first argument if not specified"
    (let [f (fnp [x] x)]
      (is (= {} (f)))))
  (testing "fnp can handle multiple dispatch"
    (let [f (fnp ([_ x] x)
                 ([_ x y] y))]
      (is (= 1 (f 1)))
      (is (= 2 (f 1 2))))))

(defnp test-defnp1
  "test function number 1"
  [_ x]
  x)

(defnp test-defnp2
  [_ x]
  x)

(defnp test-defnp3
  "test function number 3"
  ([_ x] x)
  ([_ x y] y))

(defnp ^:private test-defnp4
  "test function number 4"
  ([_ x] :bar)
  ([_ x y] :baz))

(deftest defnp-tests
  (testing "defnp can make a basic function number 1"
    (is (= 123 (test-defnp1 123))))
  (testing "defnp docstring for basic test function number 1"
    (is (= "test function number 1" (-> #'test-defnp1 meta :doc))))
  (testing "defnp doesn't need a docstring (test function number 2)"
    (is (= 123 (test-defnp2 123))))
  (testing "defnp can handle a docstring and multiple dispatch (test function number 3)"
    (is (= 123 (test-defnp3 123)))
    (is (= 123 (test-defnp3 :bar 123)))
    (is (= "test function number 3" (-> #'test-defnp3 meta :doc)))))

(comment (run-tests))

(ns chromatophore.utilities-test
  "Tests to verify that utility functions are working properly"
  (:require
   #?@(:cljs [[cljsjs.react.dom.server]
              [cljs.test :refer-macros [testing is are use-fixtures]]
              [devcards.core :refer-macros [deftest]]
              [reagent.core :as reagent]
              [reagent.ratom :refer-macros [reaction]]])
   #?(:clj [clojure.test :refer [deftest testing is are run-tests use-fixtures]])
   [chromatophore.utilities :refer [atom? #?(:cljs ratom?)]]))

(deftest atom?-basic-test
  (testing "The atom? predicate handles ordinary atoms properly"
    (is (= (atom? (atom {})) true))))

#?(:cljs
   (deftest atom?-ratom-test
     (testing "The atom? predicate handles reagent's 'ratoms'"
       (is (= (atom? (reagent/atom {})) true)))
     (testing "The ratom? predicate handles reagent's 'ratoms'"
       (is (= (ratom? (reagent/atom {})) true)))))

#?(:cljs
   (deftest atom?-reaction-test
     (testing "The atom? predicate handles reagent's 'reactions'"
       (let [a (reagent/atom {:a "foo"})
             b (reaction {:b (:a @a)})]
         (is (= (atom? a) true))
         (is (= (atom? b) true))))
     (testing "The atom? predicate handles reagent's 'reactions'"
       (let [a (reagent/atom {:a "foo"})
             b (reaction {:b (:a @a)})]
         (is (= (ratom? a) true))
         (is (= (ratom? b) true))))))

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

#?(:cljs
   (deftest ratom?-sad-paths
     (testing "The ratom? predicate isn't true of strings, numbers, etc"
       (are [x] (= (ratom? x) false)
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
         false))))

(comment (run-tests))

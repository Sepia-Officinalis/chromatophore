(ns chromatophore.utilities
  #?(:cljs (:require [reagent.ratom :refer [IReactiveAtom]]))
  #?(:clj  (:import clojure.lang.IDeref)))

(defn atom?
  "Predicate identifying if an object is an atom"
  [a]
  #?(:cljs (satisfies? IAtom a))
  #?(:clj (= (class a) clojure.lang.Atom)))

(defn deref?
  "Predicate identifying if an object can be dereferenced"
  [a]
  (satisfies? IDeref a))

#?(:cljs
   (defn ratom?
     "Predicate identifying if a object is one of regeant's 'reactive atoms'"
     [a]
     (satisfies? IReactiveAtom a)))

(ns chromatophore.toggle
  (:require [cljsjs.react.dom]
            [chromatophore.utilities :refer [ratom?]]
            [reagent.core :as reagent]
            [reagent.ratom :refer [atom] :refer-macros [run!]]))

;; TODO: Make a spec for this
;; TODO: Make this statically render
(defn
  unfold-reveal
  "`unfold-reveal` is a component with sub components you can toggle the visibility of by clicking a symbol"
  [{:keys [folded-arrow unfolded-arrow unfolded? on-toggle override on-override]
    :or   {unfolded?      false
           folded-arrow   "▶︎"
           unfolded-arrow "▼"}}]
  (assert (boolean? unfolded?)
          "Initially unfolded-state parameter must be a boolean")
  (assert (or (nil? override)
              (and (ratom? override)
                   (or (nil? @override)
                       (boolean? @override))))
          "If override is specified, it must be a ratom containing either nil or a boolean")
  (let [unfolded-state (reagent/atom unfolded?)]
    (fn unfold-reveal-fn
      ([main] (unfold-reveal-fn {} main))
      ([parameters main & children]
       (if-not (map? parameters)
         (apply unfold-reveal-fn {} parameters main children)
         (do
           (when (ratom? override)
             (run! (when (boolean? @override)
                     (reset! unfolded-state @override)
                     (if (fn? on-override) (on-override @unfolded-state)))))

           [:div.unfold-reveal
            parameters
            [:span.toggle
             (merge parameters
                    {:class    (str "unselectable "
                                    (when (empty? children)
                                      " deactivated"))
                     :on-click #(do (swap! unfolded-state not)
                                    (if (fn? on-toggle)
                                      (on-toggle % @unfolded-state)))})
             (if (and @unfolded-state
                      (not (empty? children)))
               unfolded-arrow
               folded-arrow)]
            [:div.content parameters
             [:div.main parameters main]
             [:div.children
              (merge parameters
                     (if-not @unfolded-state {:style {:display "none"}}))
              ;; TODO: better to use a child-idx function than this
              (map-indexed
               (fn [idx child] ^{:key idx} [:div.child parameters child])
               children)]]]))))))

(defn toggle-tree
  "A component for displaying a hierarchical tree of elements using `unfold-reveal` components"
  ([argument]
   (cond (or (string? argument)
             (vector? argument))
         [unfold-reveal argument]

         (map? argument)
         [toggle-tree {} argument]

         :else
         (ex-info "Cannot handle argument"
                  {:parameters nil
                   :argument   argument})))

  ([{:keys [on-toggle]
     :as   parameters}
    {:keys [main children unfolded?]
     :or   {children  []
            unfolded? false}
     :as   argument}]
   (cond (or (string? argument)
             (vector? argument))
         [unfold-reveal parameters argument]

         (map? argument)
         (let [override (reagent/atom nil)
               ;; Reactive witch-craft to get `shift-click`
               ;; to unfold-all and collapse-all
               on-override #(do (reset! override nil)
                                (reset! override %))
               new-on-toggle #(do (when (.-shiftKey %1)
                                    (on-override %2))
                                  (when (fn? on-toggle)
                                    (on-toggle %1 %2)))
               child-parameters (assoc parameters
                                       :override override)
               main-parameters (merge argument
                                      parameters
                                      {:on-toggle   new-on-toggle
                                       :on-override on-override})]
           (apply vector unfold-reveal main-parameters main
                  (map (partial toggle-tree child-parameters) children)))

         :else
         (ex-info "Cannot handle argument"
                  {:parameters parameters
                   :argument   argument}))))

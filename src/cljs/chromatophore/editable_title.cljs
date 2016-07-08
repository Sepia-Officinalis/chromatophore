(ns chromatophore.editable-title
  (:require [cljsjs.react.dom]
            [chromatophore.utils :refer [deref? ratom?]]
            [reagent.ratom
             :refer-macros [run!]
             :refer [atom]])
  (:import goog.dom.Range))

(defn- munge-value
  "Trim the whitespace and collapse multiple whitespace characters for a string value"
  [v]
  (-> v
      str
      clojure.string/trim
      (clojure.string/replace #"\s+" " ")))

(defn- escape-html
  "HTML-escape a string"
  [text]
  (-> (str text)
      (clojure.string/replace "&" "&amp;")
      (clojure.string/replace "<" "&lt;")
      (clojure.string/replace ">" "&gt;")
      (clojure.string/replace "\"" "&quot;")))

;; https://stackoverflow.com/questions/6139107/programatically-select-text-in-a-contenteditable-html-element
(defn- select-text
  ([el] (select-text el nil))
  ([el cursor-position]
   (.focus el)
   (let [range (.createFromNodeContents goog.dom.Range el)]
     (case cursor-position
       :highlight    nil
       nil           nil
       :start        (.collapse range true)
       :end          (.collapse range false)
       (ex-info "Could not process cursor-position argument"
                {:element el
                 :cursor-position cursor-position}))
     (.select range))))

(defn autofocus
  "Auto-focus a component and move the cursor to the end of the focused child"
  ([child] (autofocus {} child))
  ([{autofocus?        :autofocus
     cursor-position   :cursor-position} child]
   [(with-meta identity
      (if autofocus?
        {:component-did-mount
         #(-> %
              js/ReactDOM.findDOMNode
              (select-text cursor-position))}))
    child]))

(defn- safe-deref
  [x]
  (if (deref? x)
    (deref x)
    x))

(defn- safe-call
  [f & args]
  (if (fn? f) (apply f args)))

(defn click-to-edit
  "An `div` component that can have its value edited when clicked"
  ([text] (click-to-edit {} text))
  ([{:keys [on-save on-stop on-click on-double-click content-editable class cursor-position click-type]
     :or   {content-editable false
            save?            false
            cursor-position  :highlight
            click-type       :single
            class            ""}}
    text]
   (assert (contains? #{:single :double} click-type)
           "Click-type must be either :single or :double")
   (let [value (atom (-> text safe-deref munge-value))
         input-value (atom @value)
         editing? (if (ratom? content-editable)
                    content-editable
                    (atom content-editable))
         stop #(do (reset! input-value @value)
                   (if (fn? on-stop)
                     (on-stop %)
                     (reset! editing? false)))
         save (fn save
                ([] (save nil))
                ([event]
                 (let [v (munge-value @input-value)]
                   (when-not (some (partial = v) [@value "" " "])
                     (safe-call on-save v)
                     (reset! value v)))
                 (stop event)))]

     ;; If content-editable is changed somewhere else, save
     ;; TODO: would be nice to create a react synthetic event here...
     (if (ratom? content-editable)
       (run! (when-not @content-editable (save))))

     (fn click-to-edit-fn
       ([text] (click-to-edit-fn {} text))
       ([parameters text]
        (let [get-value #(munge-value (if (or (fn? on-save) (deref? text))
                                        (safe-deref text)
                                        @value))]
          [autofocus {:autofocus @editing?
                      :cursor-position  (safe-deref cursor-position)}
           [:div.click-to-edit
            (assoc
             parameters
             :dangerouslySetInnerHTML  (when @editing?
                                         {:__html (escape-html (get-value))})
             :class                    (clojure.string/join
                                        " "
                                        [(if @editing?
                                           "editing"
                                           "not-editing")
                                         class])

             :content-editable           @editing?

             (case click-type
               :single :on-click
               :double :on-double-click
               nil)                      #(when-not @editing?
                                            (when-not (ratom? content-editable)
                                              (reset! editing? true))
                                            (safe-call (case click-type
                                                         :single on-click
                                                         :double on-double-click
                                                         nil) %))

             :on-key-down             #(case (.-which %)
                                         13 (save %)
                                         27 (stop %)
                                         nil)

             :on-blur                 #(when @editing? (save %))

             :on-input                #(reset!
                                        input-value
                                        (-> % .-target .-textContent)))
            (when-not @editing? (get-value))]]))))))

(defn click-icon-to-edit
  "A `click-to-edit` that can be edited when a icon is clicked"
  ([html] (click-icon-to-edit {} html))
  ([{:keys [content-editable]
     :or   {content-editable false}} html]
   (let [editing?        (atom (safe-deref content-editable))
         cursor-position (atom nil)]
     (fn click-icon-to-edit-fn
       ([html] (click-icon-to-edit-fn {} html))
       ([{:keys [icon]
          :or {icon [:tt "✎"]}
          :as params}
         html]
        [:div.click-icon-to-edit
         [:span.icon
          {:class (clojure.string/join
                   " "
                   [(if @editing? "editing" "not-editing")
                    "unselectable"])
           :on-mouse-down #(do (reset! cursor-position :end)
                               (swap! editing? not))}
          icon]
         [click-to-edit
          (assoc params
                 :content-editable   editing?
                 :cursor-position    cursor-position
                 :on-click           #(do (reset! cursor-position nil)
                                          (reset! editing? true))
                 :on-stop            #(do (reset! editing? false)
                                          (reset! cursor-position nil)))
          html]])))))

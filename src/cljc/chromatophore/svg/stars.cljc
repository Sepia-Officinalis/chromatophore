(ns chromatophore.svg.stars
  (:require #?(:cljs [chromatophore.utils :refer-macros [defnp]]
               :clj [chromatophore.utils :refer [defnp]])
            [clojure.string :as string]))

(def ^:private ^:const star-height
  "Height of a star in pixels"
  129.75)

(def ^:private ^:const star-width
  "Width of a star in pixels"
  136.0)

(defnp star
  "An SVG Star"
  [{:keys [:class :fill]
    :or {:class          ""
         :fill           "red"}
    :as params}]
  [:svg {:xmlns "http://www.w3.org/2000/svg"
         :viewBox (string/join " " [208 141.75 star-width star-height])
         :class (str class " star")}
   [:path
    (merge {:fill  fill
            :stroke fill
            :stroke-width 7
            :d "M276 153l13.5 41.5H333L298 220l13.5 41.5L276 236l-35.3 25.5 13.5-41.4-35.3-25.4h43.4z"}
           params)]])

(defn check-score-and-error-assertions
  "Check the score and the error term associated with an five-star-rating"
  [score error]
  (assert (number? score) (str "Score must be a number (was " score ")"))
  (assert (number? error) (str "Error must be a number (was " error ")"))
  (assert (<= 0 score) (str "Score must not be less than 0 (was " score ")"))
  (assert (<= 0 error) (str "Error must not be less than 0 (was " error ")")))

(defnp ^:private linear-gradient
  "A simple horizontal linearGradient SVG component which fades to zero opacity"
  [{:keys [:fill]
    :or {:fill "none"}
    :as params}]
  [:linearGradient params
   [:stop {:offset "0%"
           :style {:stop-color fill
                   :stop-opacity 1}}]
   [:stop {:offset "100%"
           :style {:stop-color fill
                   :stop-opacity 0}}]])

(def five-star-rating-css-class
  "CSS Class for a five-star rating"
  ;; Default to line-height
  [:svg.five-star-rating {:height "1em"}])

(defnp ^{:style five-star-rating-css-class}
  five-star-rating
  "Represent a score from 0 to 5 with SVG stars, including partial stars for partial points"
  [{:keys [:start :score :class :error :fill :stroke]
    :or {:error 0, :fill "black", :stroke "black"}
    :as params}]
  (check-score-and-error-assertions score error)
  (let [five-stars (for [i (range 5)]
                     (nth (star {:fill "none", :stroke stroke, :transform (str "translate(" (* i star-width) ")")}) 2))
        error-gradient-id (str "error-gradient-" (hash params))]
    (-> [:svg {:xmlns "http://www.w3.org/2000/svg"
               :viewBox (string/join " " [208 141.75 (* 5 star-width) star-height])
               :class (str class " five-star-rating")}
         [:defs
          [linear-gradient {:id error-gradient-id, :fill fill}]
          (into [:clipPath {:id "stars"}] five-stars)]
         (when (< 0 (- score error))
           [:rect {:x 208, :y 141.75,
                   :height star-height,
                   :width (* (- score error) star-width),
                   :style {:clip-path "url(#stars)"},
                   :fill fill}])
         [:rect {:x (+ 207 (* (- score error) star-width)), :y 141.75,
                 :height star-height,
                 :style {:clip-path "url(#stars)"},
                 :width (* error star-width),
                 :fill (str "url(#" error-gradient-id ")")}]]
        (into five-stars))))
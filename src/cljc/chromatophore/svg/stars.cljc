(ns chromatophore.svg.stars
  (:require [clojure.string :as string]))

(def ^:private ^:const star-height
  "Height of a star in pixels"
  129.75)

(def ^:private ^:const star-width
  "Width of a star in pixels"
  136.0)

(defn star
  "An SVG Star"
  ([] (star {}))
  ([{:keys [:class :fill :stroke]
     :or {:stroke "currentColor"
          :fill   "currentColor"}
     :as params}]
   [:svg {:xmlns "http://www.w3.org/2000/svg"
          :viewBox (string/join " " [208 141.75 star-width star-height])
          :class (str class " star")}
    [:path
     (merge {:fill          fill
             :stroke        stroke
             :stroke-width  7
             :d             "M276 153l13.5 41.5H333L298 220l13.5 41.5L276 236l-35.3 25.5 13.5-41.4-35.3-25.4h43.4z"}
            params)]]))

(defn check-score-and-error-assertions
  "Check the score and the error term associated with an five-star-rating"
  [score error]
  (assert (number? score) (str "Score must be a number (was " score ")"))
  (assert (number? error) (str "Error must be a number (was " error ")"))
  (assert (<= 0 score) (str "Score must not be less than 0 (was " score ")"))
  (assert (<= 0 error) (str "Error must not be less than 0 (was " error ")")))

(defn ^:private linear-gradient
  "A simple horizontal linearGradient SVG component which fades to zero opacity"
  ([] (linear-gradient {}))
  ([{:keys [:fill]
     :or {:fill "currentColor"}
     :as params}]
   [:linearGradient params
    [:stop {:offset "0%"
            :stop-color fill
            :stop-opacity 1}]
    [:stop {:offset "100%"
            :stop-color fill
            :stop-opacity 0}]]))

(def ^:private five-stars
  (for [i (range 5)]
    (nth (star {:transform (str "translate(" (* i star-width) ")")
                :fill "none"
                :stroke "none"}) 2)))

(def five-star-rating-css-class
  "CSS Class for a five-star rating"
  ;; Default to line-height
  [:svg.five-star-rating {:height "1em"}])

(defn ^{:style five-star-rating-css-class}
  five-star-rating
  "Represent a score from 0 to 5 with SVG stars, including partial stars for partial points.

  Takes a map with the following parameters:

  - :score             A number from 0 to 5 (not optional)
  - :error             A non-negative number (defaults to 0)
  - :class             A class which the entire SVG will take on, along with 'five-star-rating' (defaults to nil)
  - :color             The color that stars, partial stars and their borders will take (defaults to nil)
  - :background-color  The background color that empty and partial stars will take (defaults to 'rgba(0,0,0,0)')
  - :fill              The fill color that stars and partial stars will take (overrides :color)
  - :stroke            The color that stars will take as their border (overrides :color)"
  ([] (five-star-rating {}))
  ([{:keys [:score :error :class :fill :stroke :background-color]
     :or {:error  0
          :background-color "rgba(0,0,0,0)"
          :fill   "currentColor"
          :stroke "currentColor"}
     :as params}]
   (check-score-and-error-assertions score error)
   [:svg {:xmlns "http://www.w3.org/2000/svg"
          :viewBox (string/join " " [208 141.75 (* 5 star-width) star-height])
          :class (str class " five-star-rating")}
    (into [:g {:color background-color, :class "star-background"}]
          (for [star five-stars]
            (assoc-in star [1 :fill] "currentColor")))
    (let [error-gradient-id (str "five-star-rating-error-gradient-" (hash params))
          stars-id          (str "five-star-rating-stars-" (hash params))]
      [:g (assoc params :class "stars")
       [:defs
        [linear-gradient {:id error-gradient-id,
                          :fill fill}]
        (into [:clipPath {:id stars-id}] five-stars)]
       (when (< 0 (- score error))
         [:rect {:x 208, :y 141.75,
                 :stroke "none"
                 :height star-height,
                 :width  (* (- score error) star-width),
                 :fill   fill
                 :style  {:clip-path (str "url(#" stars-id ")")}}])
       [:rect {:x (+ 206.75 (* (- score error) star-width)), :y 141.75,
               :height star-height
               :stroke "none"
               :style  {:clip-path (str "url(#" stars-id ")")
                        :fill (str "url(#" error-gradient-id ")")},
               :width  (* error star-width)}]])
    (into [:g (assoc params :class "star-border")]
          (for [star five-stars]
            (assoc-in star [1 :stroke] stroke)))]))

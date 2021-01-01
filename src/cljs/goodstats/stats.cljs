(ns goodstats.stats
  (:require
    [reagent.ratom :as ratom]
    [reagent.core :as reagent]
    [ajax.core :as ajax]
    [clojure.edn :as edn]
    [hodgepodge.core :refer [local-storage]]
    [clojure.string :as string]
    ["@weknow/react-bubble-chart-d3" :as BubbleChart]
    ["react-image-show" :default SlideShow]
    ["react-svg-worldmap" :refer (WorldMap)]
    ["recharts" :as recharts]
    ["react-share" :refer [FacebookShareButton TwitterShareButton TwitterIcon FacebookIcon]]
    [clojure.string :as str]))

(def TwitterShareButton (reagent/adapt-react-class TwitterShareButton))
(def TwitterIcon (reagent/adapt-react-class TwitterIcon))
(def FacebookShareButton (reagent/adapt-react-class FacebookShareButton))
(def FacebookIcon (reagent/adapt-react-class FacebookIcon))
(def WorldMap (reagent/adapt-react-class WorldMap))
(def ReactBubbleChart (reagent/adapt-react-class BubbleChart))
(def SlideShow (reagent/adapt-react-class SlideShow))
(def ResponsiveContainer (reagent/adapt-react-class recharts/ResponsiveContainer))
(def XAxis (reagent/adapt-react-class recharts/XAxis))
(def YAxis (reagent/adapt-react-class recharts/YAxis))
(def ZAxis (reagent/adapt-react-class recharts/ZAxis))
(def CartesianGrid (reagent/adapt-react-class recharts/CartesianGrid))
(def Tooltip (reagent/adapt-react-class recharts/Tooltip))
(def Legend (reagent/adapt-react-class recharts/Legend))
(def Line (reagent/adapt-react-class recharts/Line))
(def Label (reagent/adapt-react-class recharts/Label))
(def LabelList (reagent/adapt-react-class recharts/LabelList))
(def Scatter (reagent/adapt-react-class recharts/Scatter))
(def PolarAngleAxis (reagent/adapt-react-class recharts/PolarAngleAxis))


(def RadialBarChart (reagent/adapt-react-class recharts/RadialBarChart))
(def Treemap (reagent/adapt-react-class recharts/Treemap))
(def BarChart (reagent/adapt-react-class recharts/BarChart))
(def ScatterChart (reagent/adapt-react-class recharts/ScatterChart))
(def AreaChart (reagent/adapt-react-class recharts/AreaChart))
(def PieChart (reagent/adapt-react-class recharts/PieChart))
(def LineChart (reagent/adapt-react-class recharts/LineChart))
(def Bar (reagent/adapt-react-class recharts/Bar))
(def RadialBar (reagent/adapt-react-class recharts/RadialBar))
(def Area (reagent/adapt-react-class recharts/Area))
(def Pie (reagent/adapt-react-class recharts/Pie))

(defn is-portrait
  []
  (> (. js/window -innerHeight) (. js/window -innerWidth)))

(defn is-small
  []
  (> 600 (. js/window -innerHeight)))

(defn is-chonk
  []
  (and (< 1000 (. js/window -innerHeight)) (> 1300 (. js/window -innerHeight))))

(defn all-books
  [data]
  (let [books (get-in data [:book-stats :all])]
    [:div {:class "vh-100 w-100 bg-gold pa2"}
     [:div {:class "fl w-100 h-20"}
      [:h1 {:class "f-headline-ns f1 lh-title tracked-tight tc lh-solid b v-top dark-gray"}
       (str "In 2020 you've read " (count books) " books")]]
     [:div {:class "fl w-100 h-10"}
      [SlideShow {:images             (map :book-cover books)
                  :imagesHeight       "50vh" :fixedImagesHeight true
                  :infinite           true
                  :imagesHeightMobile "60vh"}]]]))

(defn average
  [data]
  (let [avg (get-in data [:book-stats :average-pages])]
    [:div {:class "vh-100 w-100 bg-black pa5"}
     [:div {:class "w-100 h-25"}]
     [:div {:class "w-100 h-50"}

      [:h1 {:class (str "f-headline-ns " (if (is-small) "f2" "f1") " lh-title tracked-tight tc lh-solid b v-mid light-red")}
       (str "Each book had an average of " avg " pages")]
      [:div {:class "w-100 h-25"}]
      ]]))

(defn longest-books
  [data]
  (let [books (get-in data [:book-stats :top-5-longest])
        largest (first (map #(edn/read-string (:num_pages %1)) books))]
    (println books)
    [:div {:class "vh-100 bg-green pa3"}
     [:div {:class "fl w-100 w-40-ns h-75-ns h-25"}
      [:h1 {:class (str "f-headline-ns " (if (is-small) "f3" "f2") " tracked-tight lh-solid b v-top black")}
       "The largest books you've read."]
      [:div {:class "h-25-ns w-100 black"}
       [:h1 {:class "f3-ns f4 lh-solid tc georgia normal black"}
        "Oh Lawd They Comin'"]]]
     [:div {:class "fl w-60-ns w-100 h-100-ns h-75"}
      [ResponsiveContainer {:width "100%" :height (if (is-portrait) "100%" "95%")}
       [BarChart {:width 800 :height 500 :data books}
        [XAxis {:stroke "black" :dataKey "title" :tick false}]
        [YAxis {:width 40 :stroke "black " :scale "linear" :orientation "left" :type "number" :domain #js [0, largest]}]
        [CartesianGrid {:stroke "black" :strokeDasharray '(3 7)}]
        [Legend]
        [Tooltip]
        [Bar {:name "Number of Pages" :dataKey "num_pages" :fill "#00000"}
         [LabelList {:dataKey "title_without_series" :position "top" :formatter (fn [arg] (first (str/split arg #":")))}]]]]]]))


(defn shortest-books
  [data]
  (let [books (get-in data [:book-stats :bottom-5-longest])
        max (last (map :num_pages books))]
    [:div {:class "vh-100 bg-navy pa3"}
     [:div {:class "fl w-60-ns w-100 h-100-ns h-75 pa3 "}
      [ResponsiveContainer {:width "95%" :height "100%"}
       [BarChart {:layout "vertical" :width 800 :height 500 :data books}
        [YAxis {:padding #js {:left 10} :type "category" :stroke "#19A974" :dataKey "title_without_series"}]
        [CartesianGrid {:stroke "#19A974" :strokeDasharray '(3 7)}]
        [XAxis {:stroke "#19A974" :scale "linear" :type "number" :domain #js [0, max]}]
        [Legend {:formatter (fn [value entry index]
                              (reagent/as-element [:span {:style {:color "#19A974"}} (. entry -value)]))}]
        [Tooltip]
        [Bar {:name "Number of Pages" :dataKey "num_pages" :fill "#19A974"}]]]]
     [:div {:class "fl  w-100 w-40-ns h-75-ns h-25"}
      [:h1 {:class "f-headline-ns f2 tracked-tight lh-solid b v-top green tr"}
       "The shortest books you've read."]
      [:div {:class "h-25 w-100 green"}
       [:h1 {:class "f3-ns f4 lh-solid tr georgia normal green"}
        "A couple of appetizers."]]]]))

(defn monthly-average
  [data]
  (let [avg (get-in data [:book-stats :average-by-month])]
    [:div {:class "vh-100 w-100 bg-navy pa5"}
     [:div {:class "fl w-100 h-100 "}
      [:div {:class "w-100 h-25"}]
      [:div {:class "w-100 h-50"}
       [:h1 {:class (str "f-headline-ns " (if (is-small) "f2" "f1") " lh-title tracked-tight tc v-mid lh-solid b gold")}
        (str "You've read " avg " books per month")]]
      [:div {:class "w-100 h-25"}]]]))

(defn books-by-month
  [data]
  [:div {:class "vh-100 bg-washed-red pa3"}
   [:div {:class "h-75 w-75-ns w-100 pt5 items-center"}
    [ResponsiveContainer {:width "100%" :height "100%"}
     [AreaChart {:width 400 :height 300 :data (get-in data [:book-stats :by-month])}
      [XAxis {:stroke "#001B44" :dataKey "month"}]
      [YAxis {:width 20 :stroke "#001B44"}]
      [CartesianGrid {:stroke "#001B44" :strokeDasharray '(3 7)}]
      [Tooltip]
      [Legend]
      [Area {:type "monotone" :name "Books Read (Cumulative)" :fill "#001B44" :stroke "#001B44" :fillOpacity 1 :dataKey "sum"}]
      [Area {:type "monotone" :name "Books Read (Absolute)" :fill "#357EDD" :stroke "#357EDD" :fillOpacity 1 :dataKey "count"}]
      ]]]
   [:div
    [:h1 {:class (str "f-headline-ns " (if (is-small) "f2" "f1") " tracked-tight lh-solid b v-top navy tr-ns tc")} "Your breakdown by month"]]])

(defn books-by-read-count
  [data]
  (let [data (get-in data [:book-stats :by-read-time])
        max (max (map :count data))]
    [:div {:class "vh-100 vh-25 bg-blue pa3 center"}
     [:div {:class "vh-25 "}
      [:h1 {:class (str "f-headline-ns " (if (is-small) "f2" "f1") " tracked-tight lh-solid b v-top washed-red tc")} "Your reading speed breakdown"]]
     [:div {:class "h-50 w-100"}
      [ResponsiveContainer {:width "100%" :height "150%"}
       [RadialBarChart {:width       "100%" :height "10%" :data data
                        :outerRadius "100%" :innerRadius "10%" :startAngle 180 :endAngle 0}
        [PolarAngleAxis {:tick false :type "number" :domain #js [0 max] :dataKey "count" :angleAxisId 0}]
        [RadialBar {:dataKey "count" :minAngle 5 :label #js {:fill "#FFDFDF"} :background #js {:fill "#FFDFDF"} :angleAxisId 0}]
        [Legend {:verticalAlign (if (is-portrait) "top" "middle")
                 :wrapperStyle  #js {:paddingTop (if (is-small) "1%" "5%")}
                 :formatter     (fn [value entry index]
                                  (reagent/as-element [:span {:style {:color "#FFDFDF"}} (. entry -value)]))
                 }]]]]]))

(defn fastest-books
  [data]
  [:div {:class "vh-100 bg-washed-green pa3"}
   [:div {:class "fl w-100 w-40-ns h-75-ns h-25"}
    [:h1 {:class (str "f-headline-ns " (if (is-small) "f2" "f1") " tracked-tight lh-solid b v-top near-black")}
     "You've read these the fastest."]]
   [:div {:class "fl w-60-ns w-100 h-100-ns h-75 pa "}
    [ResponsiveContainer {:width "100%" :height "100%"}
     [BarChart {:width 800 :height 300 :data (get-in data [:book-stats :top-5-fastest])}
      [XAxis {:tick false :stroke "#111111" :dataKey "title"}]
      [YAxis {:width 20 :stroke "#111111"}]
      [CartesianGrid {:stroke "#111111" :strokeDasharray '(3 7)}]
      [Tooltip]
      [Legend]
      [Bar {:dataKey "read-time-days" :fill "#111111" :name "Time to read (days)"}
       [LabelList {:dataKey "title_without_series" :position "top" :formatter (fn [arg] (first (str/split arg #":")))}]]]]]])

(defn slowest-books
  [data]
  (let [books (get-in data [:book-stats :bottom-5-fastest])
        max (:read-time-days (first books))]
    [:div {:class "vh-100 bg-near-black pa3"}
     [:div {:class "fl w-100 w-40-ns h-75-ns h-25"}
      [:h1 {:class (str "f-headline-ns " (if (is-small) "f2" "f1") " tracked-tight lh-solid b v-top blue tl")}
       "Your slowest reads"]
      [:div {:class "h-25 w-100 lightest-blue"}
       [:h1 {:class "f3-ns f5 lh-solid tl georgia normal blue"}
        "How many pages left??"]]]
     [:div {:class "fl w-60-ns w-100 h-100-ns h-75 pa"}
      [ResponsiveContainer {:width "100%" :height "100%"}
       [BarChart {:layout "vertical" :width 800 :height 500 :data books}
        [YAxis {:orientation "right" :type "category" :stroke "#357EDD" :dataKey "title_without_series"}]
        [CartesianGrid {:stroke "#357EDD" :strokeDasharray '(3 7)}]
        [XAxis {:stroke "#357EDD" :scale "linear" :type "number" :domain #js [0, max]}]
        [Legend {:formatter (fn [value entry index]
                              (reagent/as-element [:span {:style {:color "#357EDD"}} (. entry -value)]))}]
        [Tooltip]
        [Bar {:name "Read time (days)" :dataKey "read-time-days" :fill "#357EDD"}]
        ]]]]))

;

(defn render-custom-label
  [arg]
  (let [{:keys [cx cy midAngle innerRadius outerRadius name count]} (js->clj arg :keywordize-keys true)
        radian (/ Math/PI 180)
        radius (* 0.5 (+ innerRadius (- outerRadius innerRadius)))
        x (+ cx (* radius (Math/cos (* (- midAngle) radian))))
        y (+ cy (* radius (Math/sin (* (- midAngle) radian))))]
    (reagent/as-element
      [:text {:x                x
              :y                y
              :fill             "#FF725C"
              :textAnchor       (if (> x cx) "start" "end")
              :dominantBaseline "central"}
       (str count (if (= name "Planning on reading")
                    " planned"
                    " discovered"))])))



(defn planning-v-discovered
  [data]
  (let [planning (get-in data [:book-stats :planning])
        discovered (get-in data [:book-stats :discovered-this-year])
        all (list planning discovered)]
    [:div {:class "vh-100 bg-light-red pa3"}
     [:div {:class "fl w-100 h-25"}
      [:h1 {:class "f-headline-ns f1 tracked-tight lh-solid b v-top navy tl"}
       "Are you spontaneous or a planner?"]]
     [:div {:class "fl w-100 h-75 pa"}
      [ResponsiveContainer {:width "100%" :height "100%"}
       [PieChart {:width 800 :height 500}
        [Pie {:data      all :dataKey "count"
              :nameKey   "name" :outerRadius "90%" :innerRadius "30%" :fill "#001B44"
              :labelLine (if (is-portrait) false true)
              :label     (if (is-portrait)
                           render-custom-label
                           (fn [entry] (str (. entry -name) ", " (. entry -value) " books")))
              :stroke    "#FF725C"}
         ]]]]]))

(defn ratings-by-size
  [data]
  (let [books (get-in data [:book-stats :rating-by-page-count])]
    [:div {:class "vh-100 bg-gold pa3"}
     [:div {:class ""}
      [:h1 {:class (str "f-headline-ns " (if (is-small) "f1" "f-subheadline") " tracked-tight lh-solid b v-top navy tr")}
       "Does size matter?"]]
     [:div {:class "fl w-100 h-50"}
      [ResponsiveContainer {:width "100%" :height "120%"}
       [ScatterChart {:width 100 :height 100}
        [XAxis {:dataKey "pages" :stroke "#001B44" :name "Number of Pages" :unit " pages" :type "number"}]
        [YAxis {:width 30 :dataKey "rating" :stroke "#001B44" :name "Rating" :unit " stars" :type "number"
                :ticks [0 1 2 3 4 5] :domain #js [0 5]}]
        [ZAxis {:dataKey "title" :stroke "#001B44" :name "Title" :type "category"}]

        [CartesianGrid {:stroke "#001B44" :strokeDasharray '(3 7)}]
        [Scatter {:data books :name "Books Read" :fill "#001B44"}]
        [Legend {:formatter (fn [value entry index]
                              (reagent/as-element [:span {:style {:color "#001B44"}} (. entry -value)]))}]
        [Tooltip]]]]
     ]))

(defn favorite-authors
  [data]
  (let [books (get-in data [:author-stats :favorite])
        grouped-books (group-by #(list (:review-score %) (:review-count %)) books)
        final-data (map (fn
                          [[key elements]]
                          (do (println elements)
                              (reduce #(hash-map
                                         :label (if (= 1 (count elements))
                                                  (:author %1)
                                                  (str (count elements) " authors"))
                                         :author (if (= (:author %1) (:author %2))
                                                   (:author %2)
                                                   (str (:author %1) ", " (:author %2)))
                                         :review-score (:review-score %1)
                                         :review-count (:review-count %1))
                                      (first elements)
                                      elements)))
                        grouped-books)]
    [:div {:class "vh-100 bg-light-yellow pa3"}
     [:div {:class (str "fl " (if (is-portrait) "w-100 h-25" "w-40-ns h-75-ns"))}
      [:h1 {:class (str "f-headline-ns " (if (is-small) "f2" "f1") " tracked-tight lh-solid b v-top dark-gray tl")}
       "Your favorite authors"]]
     [:div {:class (str "fl pa " (if (is-portrait) "w-100 h-75" "w-60-ns h-100-ns"))}
      [ResponsiveContainer {:width "100%" :height "100%"}
       [ScatterChart {:width 100 :height 100}
        [XAxis {:dataKey "review-count" :stroke "#333333" :name "Number of books read" :unit " books" :type "number"}]
        [YAxis {:width 30 :dataKey "review-score" :stroke "#333333" :name "Rating" :unit " stars" :type "number"
                :ticks [0 1 2 3 4 5] :domain #js [0 5]}]
        [ZAxis {:dataKey "author" :stroke "#333333" :name "Authors" :type "category"}]

        [CartesianGrid {:stroke "#333333" :strokeDasharray '(3 7)}]
        [Scatter {:data final-data :name "Author" :fill "#333333"}
         [LabelList {:dataKey "label" :fill "#333333" :position "left" :offset 10}]]
        [Legend {:formatter (fn [value entry index]
                              (reagent/as-element [:span {:style {:color "#333333"}} (. entry -value)]))}]
        [Tooltip]]]]]))


(defn world-map
  [data]
  [:div {:class "vh-100 bg-dark-green pa3 center"}
   [:div
    [:h1 {:class "f-headline-ns f1  tracked-tight lh-solid b v-top light-yellow tc"} "Your authors by country"]]
   [:div {:class "tc-l"}
    [WorldMap {:color           "#FBF1A9" :value-prefix "-" :fillOpacity 1
               :frame           false :size "xl" :data (filter #(not (nil? (:country %))) (get-in data [:author-stats :country]))
               :backgroundColor "#137752" :borderColor "#FBF1A9"
               :strokeOpacity   1
               :styleFunction   (fn [context]
                                  #js {:strokeOpacity 1
                                       :strokeWidth   1
                                       :stroke        "#137752"
                                       :fill          "#FBF1A9"
                                       :fillOpacity   1})}]]])

(defn all-authors
  [data]
  (let [authors (get-in data [:author-stats :all])]
    [:div {:class "vh-100 w-100 bg-dark-gray pa2 "}
     [:div {:class "w-100 h-25"}]
     [:div {:class "w-100"}
      [:h1 {:class (str (if (is-small) "f1" "f-subheadline")" tracked-tight tc lh-solid b v-btm washed-green")}
       (str "In 2020 you've read " (count authors) " authors")]]
     [:div {:class "w-100 h-25"}]
     ]))

(defn genres-map-2
  [data]
  (let [books (get-in data [:genre-stats :all])]
    [:div {:class "vh-100 bg-navy pa3"}
     [:div {:class (str (if (is-portrait) " w-100" "w-50-ns h-75-ns") " fl")}
      [:h1 {:class (str "f-headline-ns f1 tracked-tight lh-solid b v-top gold " (if (is-portrait) "tr " "tl ")  (if (is-small) "f2" "mv2"))}
       "These were the genres you read the most"]
      [:div {:class (str (if (is-portrait) "h-25" "h-25-ns") " w-100 gold")}
       [:h1 {:class "f3-ns f5 lh-solid tc georgia normal gold"}
        "Wow, so ecletic!"]]]

     [:div {:class (str (if (is-portrait) (str "w-100 h-25 " (if (is-chonk) "chonk" "")) "w-50-ns h-100-ns mw7 v-btm") " fl")}
      [ReactBubbleChart {
                         :graph      #js {:zoom 1}
                         :width      100
                         :height     100
                         :valueFont  #js {:size 1.5 :family "Arial" :color "#001B44" :weight "bold"}
                         :labelFont  #js {:size 1.5 :family "Arial" :color "#001B44" :weight "bold"}
                         :showLegend false
                         :data       books
                         }]]
     ]))

(defn the-end
  [data]
  (let [books (count (get-in data [:book-stats :all]))]
    [:div {:class "vh-100 w-100 bg-light-green pa2 center"}
     [:h1 {:class (str "f-headline-ns " (if (is-small) "f1" "f-subheadline") " tracked-tight tc lh-solid b v-btm washed-green")}
      "That's all for this year. See you in 2021!"]
     [:div {:class "w-100 tc pa2"}
      [:h1 {:class "f1-ns f3 tracked-tight tc lh-solid b v-btm washed-green"}
       "Share with your friends"]
      [:> TwitterShareButton {:url "http://readingyear.com" :title (str "I've read " books " books in 2020. What about you?")}
       [:> TwitterIcon {:size 32 :round true}]]
      [:> FacebookShareButton {:url "http://readingyear.com" :quote (str "I've read " books " books in 2020. What about you?")}
       [:> FacebookIcon {:size 32 :round true}]]]]))


(defn statistics-component [books]
  (fn [] (let [books-data (get local-storage :reading-review-data {})]
           [:div
            (all-books books-data)
            (genres-map-2 books-data)
            (planning-v-discovered books-data)
            (average books-data)
            (longest-books books-data)
            (shortest-books books-data)
            (ratings-by-size books-data)
            (monthly-average books-data)
            (books-by-month books-data)
            (books-by-read-count books-data)
            (slowest-books books-data)
            (fastest-books books-data)
            (all-authors books-data)
            (favorite-authors books-data)
            (world-map books-data)
            (the-end books-data)
            ])))









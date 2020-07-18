(ns goodstats.user-test
  (:require [clojure.test :refer :all])
  (:require [goodstats.user :refer [shelf->map]]
            [clj-http.client :as client]
            [clojure.data.xml :as data]))

(deftest shelf->map-test
  (let [xml-response {:body "<GoodreadsResponse>
                    <shelves start=\"1\" end=\"100\" total=\"125\">
                      <user_shelf>
                        <id type=\"integer\">5625761</id>
                        <name>read</name>
                        <book_count type=\"integer\">526</book_count>
                        <exclusive_flag type=\"boolean\">true</exclusive_flag>
                        <sort></sort>
                        <order nil=\"true\"/>
                        <per_page type=\"integer\" nil=\"true\"/>
                        <display_fields></display_fields>
                        <featured type=\"boolean\">true</featured>
                        <recommend_for type=\"boolean\">true</recommend_for>
                        <sticky type=\"boolean\" nil=\"true\"/>
                      </user_shelf>
                    </shelves>
                  </GoodreadsResponse>"}

        parsed-response '#clojure.data.xml.Element{:tag     :GoodreadsResponse,
                                                   :attrs   {},
                                                   :content (#clojure.data.xml.Element{:tag     :shelves,
                                                                                       :attrs   {:start "1", :end "100", :total "125"},
                                                                                       :content (#clojure.data.xml.Element{:tag     :user_shelf,
                                                                                                                           :attrs   {},
                                                                                                                           :content (#clojure.data.xml.Element{:tag     :id,
                                                                                                                                                               :attrs   {:type "integer"},
                                                                                                                                                               :content ("5625761")}
                                                                                                                                     #clojure.data.xml.Element{:tag     :name,
                                                                                                                                                               :attrs   {},
                                                                                                                                                               :content ("read")}
                                                                                                                                     #clojure.data.xml.Element{:tag     :book_count,
                                                                                                                                                               :attrs   {:type "integer"},
                                                                                                                                                               :content ("526")}
                                                                                                                                     #clojure.data.xml.Element{:tag     :exclusive_flag,
                                                                                                                                                               :attrs   {:type "boolean"},
                                                                                                                                                               :content ("true")}
                                                                                                                                     #clojure.data.xml.Element{:tag     :sort,
                                                                                                                                                               :attrs   {},
                                                                                                                                                               :content ()}
                                                                                                                                     #clojure.data.xml.Element{:tag     :order,
                                                                                                                                                               :attrs   {:nil "true"},
                                                                                                                                                               :content ()}
                                                                                                                                     #clojure.data.xml.Element{:tag     :per_page,
                                                                                                                                                               :attrs   {:type "integer",
                                                                                                                                                                         :nil  "true"},
                                                                                                                                                               :content ()}
                                                                                                                                     #clojure.data.xml.Element{:tag     :display_fields,
                                                                                                                                                               :attrs   {},
                                                                                                                                                               :content ()}
                                                                                                                                     #clojure.data.xml.Element{:tag     :featured,
                                                                                                                                                               :attrs   {:type "boolean"},
                                                                                                                                                               :content ("true")}
                                                                                                                                     #clojure.data.xml.Element{:tag     :recommend_for,
                                                                                                                                                               :attrs   {:type "boolean"},
                                                                                                                                                               :content ("true")}
                                                                                                                                     #clojure.data.xml.Element{:tag     :sticky,
                                                                                                                                                               :attrs   {:type "boolean",
                                                                                                                                                                         :nil  "true"},
                                                                                                                                                               :content ()})})})}]

    (with-redefs [client/get (fn [url query-params] xml-response)]
      (testing "Retrieve Shelf XML"
        (is (= (goodstats.user/get-shelf-response "1" "Key") parsed-response))))))

(deftest shelf->map-test
  (let [shelf-content '{:user_shelf ({:id ("5625761")}
                                     {:name ("read")}
                                     {:book_count ("526")}
                                     {:exclusive_flag ("true")}
                                     {:sort ()}
                                     {:order ()}
                                     {:per_page ()}
                                     {:display_fields ()}
                                     {:featured ("true")}
                                     {:recommend_for ("true")}
                                     {:sticky ()})}
        map '{:book_count     ("526"),
              :exclusive_flag ("true"),
              :display_fields (),
              :name           ("read"),
              :featured       ("true"),
              :sticky         (),
              :id             ("5625761"),
              :recommend_for  ("true"),
              :order          (),
              :per_page       (),
              :sort           ()}]
    (testing "Converting map of list of maps to a single map"
      (is (= (shelf->map shelf-content) map)))))
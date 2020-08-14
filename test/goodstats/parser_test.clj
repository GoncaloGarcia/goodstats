(ns goodstats.parser-test
  (:require [clojure.test :refer :all])
  (:require [goodstats.parser :refer [filter-unecessary-keys]]
            [goodstats.parser :as parser]
            [clojure.data.xml :as data]))

(deftest filter-unecessary-keys-test
  (testing "Correctly removes key from map"
    (is (= {}
           (parser/filter-unecessary-keys {:a "b"} :a)))))

(deftest map-list->map-test
  (testing "Transforms list with two maps into a single map"
    (is (= {:a "a" :b "b"}
           (parser/map-list->map '({:a "a"} {:b "b"}))))))

(deftest xml->map-test
  (let [xml-test (get (data/parse-str "<GoodreadsResponse>
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
                  </GoodreadsResponse>") :content)

        expected-result  '({:shelves ({:user_shelf ({:id "5625761"}
                                                    {:name "read"}
                                                    {:book_count "526"}
                                                    {:exclusive_flag "true"}
                                                    {:sort ()}
                                                    {:order ()}
                                                    {:per_page ()}
                                                    {:display_fields ()}
                                                    {:featured "true"}
                                                    {:recommend_for "true"}
                                                    {:sticky ()})})})]
    (testing "Transform xml"
      (is (= (parser/xml->map xml-test) expected-result)))))

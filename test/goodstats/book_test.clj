(ns goodstats.book-test
  (:require [clojure.test :refer :all])
  (:require [goodstats.book :refer [get-book-response]]
            [clj-http.client :as client]))

(deftest get-book-response-test
  (let [book {:body "<book>
                  <publication_year>2006</publication_year>
                  <publication_month>9</publication_month>
                  <publication_day>16</publication_day>
              </book>"}

        xml-book '#clojure.data.xml.Element{:tag     :book,
                                            :attrs   {},
                                            :content (#clojure.data.xml.Element{:tag :publication_year, :attrs {}, :content ("2006")}
                                                      #clojure.data.xml.Element{:tag :publication_month, :attrs {}, :content ("9")}
                                                      #clojure.data.xml.Element{:tag :publication_day, :attrs {}, :content ("16")})}]
    (with-redefs [client/get (fn [url query-params] book)]
      (testing "Retrieve Book XML"
        (is (= (goodstats.book/get-book-response "1" "Key") xml-book))))))


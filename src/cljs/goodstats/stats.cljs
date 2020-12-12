(ns goodstats.stats
  (:require [reagent.ratom :as ratom]
            [reagent.core :as reagent]
            [ajax.core :as ajax]
            [clojure.edn :as edn]
            ["recharts" :as recharts]))


(def ResponsiveContainer (reagent/adapt-react-class recharts/ResponsiveContainer))
(def XAxis (reagent/adapt-react-class recharts/XAxis))
(def YAxis (reagent/adapt-react-class recharts/YAxis))
(def CartesianGrid (reagent/adapt-react-class recharts/CartesianGrid))
(def Tooltip (reagent/adapt-react-class recharts/Tooltip))
(def Legend (reagent/adapt-react-class recharts/Legend))
(def Line (reagent/adapt-react-class recharts/Line))
(def Label (reagent/adapt-react-class recharts/Label))
(def PolarAngleAxis (reagent/adapt-react-class recharts/PolarAngleAxis))


(def RadialBarChart (reagent/adapt-react-class recharts/RadialBarChart))
(def Treemap (reagent/adapt-react-class recharts/Treemap))
(def BarChart (reagent/adapt-react-class recharts/BarChart))
(def AreaChart (reagent/adapt-react-class recharts/AreaChart))
(def PieChart (reagent/adapt-react-class recharts/PieChart))
(def LineChart (reagent/adapt-react-class recharts/LineChart))
(def Bar (reagent/adapt-react-class recharts/Bar))
(def RadialBar (reagent/adapt-react-class recharts/RadialBar))
(def Area (reagent/adapt-react-class recharts/Area))
(def Pie (reagent/adapt-react-class recharts/Pie))

(def data {:book-stats   {:all '({:book-cover "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1599537296l/43207719._SX318_.jpg",
                                 :title "Ayoade on Top"}
                                {:book-cover "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1447167669l/27782940._SX318_.jpg",
                                 :title "Ensaio Sobre a Cegueira"}
                                {:book-cover "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1466299641l/30646587._SY475_.jpg",
                                 :title "The Simple Path to Wealth: Your road map to financial independence and a rich, free life"}
                                {:book-cover "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1328347100l/11837314.jpg",
                                 :title "The Worldly Philosophers"}
                                {:book-cover "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1361736869l/17412573.jpg",
                                 :title "The People in the Trees"}
                                {:book-cover "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1412644327l/63032.jpg",
                                 :title "2666"}
                                {:book-cover "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1475695315l/18045891.jpg",
                                 :title "Sharp Objects"}
                                {:book-cover "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1590530965l/49771934._SY475_.jpg",
                                 :title "Say Nothing: A True Story of Murder and Memory in Northern Ireland"}
                                {:book-cover "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1550850779l/16271._SY475_.jpg",
                                 :title "The Intuitionist"}
                                {:book-cover "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1533135682l/41021501._SY475_.jpg",
                                 :title "Small Great Things"}
                                {:book-cover "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1493178362l/30555488.jpg",
                                 :title "The Underground Railroad"}
                                {:book-cover "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1599649084l/30753841._SX318_.jpg",
                                 :title "Salt, Fat, Acid, Heat: Mastering the Elements of Good Cooking"}
                                {:book-cover "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1550721459l/6952.jpg",
                                 :title "Like Water for Chocolate"}
                                {:book-cover "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1385140172l/18886293.jpg",
                                 :title "By Night in Chile"}
                                {:book-cover "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1327881361l/320.jpg",
                                 :title "One Hundred Years of Solitude"}
                                {:book-cover "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1498631519l/95558.jpg",
                                 :title "Solaris"}
                                {:book-cover "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1281419771l/888628.jpg",
                                 :title "Neuromancer (Sprawl, #1)"}
                                {:book-cover "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1386924361l/11297.jpg",
                                 :title "Norwegian Wood"}
                                {:book-cover "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1348445281l/5544.jpg",
                                 :title "Surely You're Joking, Mr. Feynman!: Adventures of a Curious Character"}),
                          :by-read-time '({:name "1 week",
                                          :count 5,
                                           :fill "#9EEBCF",
                                          :books '({:read-time-days 6.9583335, :title "The Underground Railroad"}
                                                  {:read-time-days 6.6666665,
                                                   :title "The Simple Path to Wealth: Your road map to financial independence and a rich, free life"}
                                                  {:read-time-days 6.6666665, :title "Ensaio Sobre a Cegueira"}
                                                  {:read-time-days 3.9166667, :title "Sharp Objects"}
                                                  {:read-time-days 3.5833333, :title "Solaris"})}
                                         {:name "2 weeks",
                                          :count 7,
                                          :fill "#FF6300"
                                          :books '({:read-time-days 13.208333, :title "The Intuitionist"}
                                                  {:read-time-days 11.791667,
                                                   :title "Say Nothing: A True Story of Murder and Memory in Northern Ireland"}
                                                  {:read-time-days 10.041667, :title "Norwegian Wood"}
                                                  {:read-time-days 8.333333,
                                                   :title "Surely You're Joking, Mr. Feynman!: Adventures of a Curious Character"}
                                                  {:read-time-days 8.041667, :title "Ayoade on Top"}
                                                  {:read-time-days 7.9583335, :title "Neuromancer (Sprawl, #1)"}
                                                  {:read-time-days 7.7916665, :title "Small Great Things"})}
                                         {:name "1 month",
                                          :count 4,
                                          :fill "#19A974"
                                          :books '({:read-time-days 25.25, :title "Salt, Fat, Acid, Heat: Mastering the Elements of Good Cooking"}
                                                  {:read-time-days 17.583334, :title "The Worldly Philosophers"}
                                                  {:read-time-days 17.0, :title "By Night in Chile"}
                                                  {:read-time-days 16.625, :title "The People in the Trees"})}
                                         {:name "2 months",
                                          :count 2,
                                          :fill "#FFD700"
                                          :books '({:read-time-days 47.0, :title "One Hundred Years of Solitude"}
                                                  {:read-time-days 40.0, :title "2666"})}
                                         {:name "4 months", :fill "#00449E" :count 2, :books '()}
                                         {:name "8 months", :count 7, :fill "#A463F2" :books '()}
                                         {:name "1 year", :count 5, :fill "#FF80CC" :books '()}),
                          :planning {:name "Planning on reading",
                                     :count 7,
                                     :data #{"By Night in Chile"
                                             "Like Water for Chocolate"
                                             "Surely You're Joking, Mr. Feynman!: Adventures of a Curious Character"
                                             "One Hundred Years of Solitude"
                                             "Solaris"
                                             "Neuromancer (Sprawl, #1)"
                                             "Norwegian Wood"}}
                          :discovered-this-year {:name "Discovered this year",
                                                  :count 12,
                                                  :data #{"The Worldly Philosophers"
                                                          "Ayoade on Top"
                                                          "The Simple Path to Wealth: Your road map to financial independence and a rich, free life"
                                                          "Salt, Fat, Acid, Heat: Mastering the Elements of Good Cooking"
                                                          "Say Nothing: A True Story of Murder and Memory in Northern Ireland"
                                                          "Ensaio Sobre a Cegueira"
                                                          "The People in the Trees"
                                                          "The Underground Railroad"
                                                          "Small Great Things"
                                                          "Sharp Objects"
                                                          "2666"
                                                          "The Intuitionist"}},
                          :top-5-longest        '({:title "2666", :num_pages "1128"}
                                                  {:title "Small Great Things", :num_pages "510"}
                                                  {:title "Salt, Fat, Acid, Heat: Mastering the Elements of Good Cooking", :num_pages "480"}
                                                  {:title     "Say Nothing: A True Story of Murder and Memory in Northern Ireland",
                                                   :num_pages "464"}
                                                  {:title "One Hundred Years of Solitude", :num_pages 417}),
                          :bottom-5-longest     '({:title "Solaris", :num_pages 204}
                                                  {:title "Like Water for Chocolate", :num_pages 222}
                                                  {:title "Sharp Objects", :num_pages 254}
                                                  {:title "The Intuitionist", :num_pages 255}
                                                  {:title "Ayoade on Top", :num_pages 256}),
                          :top-5-fastest        '({:title "Solaris", :read-time-days 3.5833333}
                                                  {:title "Sharp Objects", :read-time-days 3.9166667}
                                                  {:title "Ensaio Sobre a Cegueira", :read-time-days 6.6666665}
                                                  {:title          "The Simple Path to Wealth: Your road map to financial independence and a rich, free life",
                                                   :read-time-days 6.6666665}
                                                  {:title "The Underground Railroad", :read-time-days 6.9583335}),
                          :bottom-5-fastest     '({:title "One Hundred Years of Solitude", :read-time-days 47.0}
                                                  {:title "2666", :read-time-days 40.0}
                                                  {:title          "Salt, Fat, Acid, Heat: Mastering the Elements of Good Cooking",
                                                   :read-time-days 25.25}
                                                  {:title "The Worldly Philosophers", :read-time-days 17.583334}
                                                  {:title "By Night in Chile", :read-time-days 17.0})
                          :by-month             [{:books [{:title "The Underground Railroad", :read-at "Fri Jan 24 11:29:15 -0800 2020"}
                                                          {:title   "Salt, Fat, Acid, Heat: Mastering the Elements of Good Cooking",
                                                           :read-at "Sun Jan 26 14:13:36 -0800 2020"}
                                                          {:title "Like Water for Chocolate", :read-at "Thu Jan 16 13:06:06 -0800 2020"}
                                                          {:title "Neuromancer (Sprawl, #1)", :read-at "Mon Jan 13 14:35:34 -0800 2020"}
                                                          {:title   "Surely You're Joking, Mr. Feynman!: Adventures of a Curious Character",
                                                           :read-at "Fri Jan 03 10:16:21 -0800 2020"}],
                                                  :month "Jan",
                                                  :count 5,
                                                  :sum   5}
                                                 {:books [{:title "Small Great Things", :read-at "Wed Feb 05 01:53:13 -0800 2020"}
                                                          {:title "Norwegian Wood", :read-at "Sat Feb 15 16:28:48 -0800 2020"}],
                                                  :month "Feb",
                                                  :count 2,
                                                  :sum   7}
                                                 {:books [{:title "Sharp Objects", :read-at "Thu Mar 19 14:41:56 -0700 2020"}
                                                          {:title   "Say Nothing: A True Story of Murder and Memory in Northern Ireland",
                                                           :read-at "Sun Mar 15 11:22:36 -0700 2020"}
                                                          {:title "The Intuitionist", :read-at "Mon Mar 02 15:28:52 -0800 2020"}],
                                                  :month "Mar",
                                                  :count 3,
                                                  :sum   10}
                                                 {:books [{:title "The People in the Trees", :read-at "Sun May 17 06:40:30 -0700 2020"}],
                                                  :month "May",
                                                  :count 1,
                                                  :sum   11}
                                                 {:books [{:title "Ensaio Sobre a Cegueira", :read-at "Thu Jun 11 08:14:08 -0700 2020"}
                                                          {:title   "The Simple Path to Wealth: Your road map to financial independence and a rich, free life",
                                                           :read-at "Thu Jun 11 06:42:04 -0700 2020"}
                                                          {:title "The Worldly Philosophers", :read-at "Thu Jun 04 05:56:43 -0700 2020"}],
                                                  :month "Jun",
                                                  :count 3,
                                                  :sum   14}
                                                 {:books [{:title "One Hundred Years of Solitude", :read-at "Tue Jul 28 15:36:51 -0700 2020"}],
                                                  :month "Jul",
                                                  :count 1,
                                                  :sum   15}
                                                 {:books [{:title "Ayoade on Top", :read-at "Sun Aug 30 13:52:27 -0700 2020"}
                                                          {:title "Solaris", :read-at "Sun Aug 02 05:50:27 -0700 2020"}],
                                                  :month "Aug",
                                                  :count 2,
                                                  :sum   17}
                                                 {:books nil, :month "Sep", :count 0, :sum 17}
                                                 {:books [{:title "By Night in Chile", :read-at "Sun Oct 18 00:00:00 -0700 2020"}],
                                                  :month "Oct",
                                                  :count 1,
                                                  :sum   18}
                                                 {:books nil, :month "Nov", :count 0, :sum 18}
                                                 {:books nil, :month "Dec", :count 0, :sum 18}]},
           :author-stats {:all        '({:rating           "4",
                                         :author-link      "https://www.goodreads.com/author/show/13450.Gabriel_Garc_a_M_rquez",
                                         :author-id        "13450",
                                         :author-name      "Gabriel García Márquez",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1588856705p5/13450.jpg\n",
                                         :title            "One Hundred Years of Solitude",
                                         :country          "Colombia",
                                         :avg-rating       4}
                                        {:rating           "4",
                                         :author-link      "https://www.goodreads.com/author/show/2936728.J_L_Collins",
                                         :author-id        "2936728",
                                         :author-name      "J.L. Collins",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1507657999p5/2936728.jpg\n",
                                         :title            "The Simple Path to Wealth: Your road map to financial independence and a rich, free life",
                                         :country          "",
                                         :avg-rating       4}
                                        {:rating           '("3" "5"),
                                         :author-link      "https://www.goodreads.com/author/show/10029.Colson_Whitehead",
                                         :author-id        "10029",
                                         :author-name      "Colson Whitehead",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1561996933p5/10029.jpg\n",
                                         :title            '("The Intuitionist" "The Underground Railroad"),
                                         :country          "The United States",
                                         :avg-rating       4}
                                        {:rating           "5",
                                         :author-link      "https://www.goodreads.com/author/show/7128.Jodi_Picoult",
                                         :author-id        "7128",
                                         :author-name      "Jodi Picoult",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1586468459p5/7128.jpg\n",
                                         :title            "Small Great Things",
                                         :country          "The United States",
                                         :avg-rating       5}
                                        {:rating           "5",
                                         :author-link      "https://www.goodreads.com/author/show/2383.Gillian_Flynn",
                                         :author-id        "2383",
                                         :author-name      "Gillian Flynn",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1232123231p5/2383.jpg\n",
                                         :title            "Sharp Objects",
                                         :country          "The United States",
                                         :avg-rating       5}
                                        {:rating           "4",
                                         :author-link      "https://www.goodreads.com/author/show/4468597.Richard_Ayoade",
                                         :author-id        "4468597",
                                         :author-name      "Richard Ayoade",
                                         :author-image_url "
                                          https://s.gr-assets.com/assets/nophoto/user/m_200x266-d279b33f8eec0f27b7272477f09806be.png
                                          ",
                                         :title            "Ayoade on Top",
                                         :country          "The United Kingdom",
                                         :avg-rating       4}
                                        {:rating           "5",
                                         :author-link      "https://www.goodreads.com/author/show/4694.Laura_Esquivel",
                                         :author-id        "4694",
                                         :author-name      "Laura Esquivel",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1457529590p5/4694.jpg\n",
                                         :title            "Like Water for Chocolate",
                                         :country          "Mexico",
                                         :avg-rating       5}
                                        {:rating           "5",
                                         :author-link      "https://www.goodreads.com/author/show/45396.Robert_L_Heilbroner",
                                         :author-id        "45396",
                                         :author-name      "Robert L. Heilbroner",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1270842051p5/45396.jpg\n",
                                         :title            "The Worldly Philosophers",
                                         :country          "The United States",
                                         :avg-rating       5}
                                        {:rating           '("5" "4"),
                                         :author-link      "https://www.goodreads.com/author/show/72039.Roberto_Bola_o",
                                         :author-id        "72039",
                                         :author-name      "Roberto Bolaño",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1579028991p5/72039.jpg\n",
                                         :title            '("2666" "By Night in Chile"),
                                         :country          "Chile",
                                         :avg-rating       4.5}
                                        {:rating           "5",
                                         :author-link      "https://www.goodreads.com/author/show/1429989.Richard_P_Feynman",
                                         :author-id        "1429989",
                                         :author-name      "Richard P. Feynman",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1498916887p5/1429989.jpg\n",
                                         :title            "Surely You're Joking, Mr. Feynman!: Adventures of a Curious Character",
                                         :country          "The United States",
                                         :avg-rating       5}
                                        {:rating           "5",
                                         :author-link      "https://www.goodreads.com/author/show/1285555.Jos_Saramago",
                                         :author-id        "1285555",
                                         :author-name      "José Saramago",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1497455560p5/1285555.jpg\n",
                                         :title            "Ensaio Sobre a Cegueira",
                                         :country          "Portugal",
                                         :avg-rating       5}
                                        {:rating           "3",
                                         :author-link      "https://www.goodreads.com/author/show/10991.Stanis_aw_Lem",
                                         :author-id        "10991",
                                         :author-name      "Stanisław Lem",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1246185166p5/10991.jpg\n",
                                         :title            "Solaris",
                                         :country          "Ukraine",
                                         :avg-rating       3}
                                        {:rating           "4",
                                         :author-link      "https://www.goodreads.com/author/show/9226.William_Gibson",
                                         :author-id        "9226",
                                         :author-name      "William Gibson",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1373826214p5/9226.jpg\n",
                                         :title            "Neuromancer (Sprawl, #1)",
                                         :country          "The United States",
                                         :avg-rating       4}
                                        {:rating           "4",
                                         :author-link      "https://www.goodreads.com/author/show/3354.Haruki_Murakami",
                                         :author-id        "3354",
                                         :author-name      "Haruki Murakami",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1539035376p5/3354.jpg\n",
                                         :title            "Norwegian Wood",
                                         :country          "Japan",
                                         :avg-rating       4}
                                        {:rating           "5",
                                         :author-link      "https://www.goodreads.com/author/show/15401886.Samin_Nosrat",
                                         :author-id        "15401886",
                                         :author-name      "Samin Nosrat",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1505316013p5/15401886.jpg\n",
                                         :title            "Salt, Fat, Acid, Heat: Mastering the Elements of Good Cooking",
                                         :country          "",
                                         :avg-rating       5}
                                        {:rating           "5",
                                         :author-link      "https://www.goodreads.com/author/show/197852.Patrick_Radden_Keefe",
                                         :author-id        "197852",
                                         :author-name      "Patrick Radden Keefe",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1538163619p5/197852.jpg\n",
                                         :title            "Say Nothing: A True Story of Murder and Memory in Northern Ireland",
                                         :country          "The United States",
                                         :avg-rating       5}
                                        {:rating           "4",
                                         :author-link      "https://www.goodreads.com/author/show/6571447.Hanya_Yanagihara",
                                         :author-id        "6571447",
                                         :author-name      "Hanya Yanagihara",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1421881815p5/6571447.jpg\n",
                                         :title            "The People in the Trees",
                                         :country          "",
                                         :avg-rating       4}),
                          :most-read  '({:rating           '("5" "4"),
                                         :author-link      "https://www.goodreads.com/author/show/72039.Roberto_Bola_o",
                                         :author-id        "72039",
                                         :author-name      "Roberto Bolaño",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1579028991p5/72039.jpg\n",
                                         :title            '("2666" "By Night in Chile"),
                                         :country          "Chile",
                                         :avg-rating       4.5}
                                        {:rating           '("3" "5"),
                                         :author-link      "https://www.goodreads.com/author/show/10029.Colson_Whitehead",
                                         :author-id        "10029",
                                         :author-name      "Colson Whitehead",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1561996933p5/10029.jpg\n",
                                         :title            '("The Intuitionist" "The Underground Railroad"),
                                         :country          "The United States",
                                         :avg-rating       4}
                                        {:rating           "4",
                                         :author-link      "https://www.goodreads.com/author/show/6571447.Hanya_Yanagihara",
                                         :author-id        "6571447",
                                         :author-name      "Hanya Yanagihara",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1421881815p5/6571447.jpg\n",
                                         :title            "The People in the Trees",
                                         :country          "",
                                         :avg-rating       4}
                                        {:rating           "5",
                                         :author-link      "https://www.goodreads.com/author/show/197852.Patrick_Radden_Keefe",
                                         :author-id        "197852",
                                         :author-name      "Patrick Radden Keefe",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1538163619p5/197852.jpg\n",
                                         :title            "Say Nothing: A True Story of Murder and Memory in Northern Ireland",
                                         :country          "The United States",
                                         :avg-rating       5}
                                        {:rating           "5",
                                         :author-link      "https://www.goodreads.com/author/show/15401886.Samin_Nosrat",
                                         :author-id        "15401886",
                                         :author-name      "Samin Nosrat",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1505316013p5/15401886.jpg\n",
                                         :title            "Salt, Fat, Acid, Heat: Mastering the Elements of Good Cooking",
                                         :country          "",
                                         :avg-rating       5}),
                          :best-rated '({:rating           "5",
                                         :author-link      "https://www.goodreads.com/author/show/197852.Patrick_Radden_Keefe",
                                         :author-id        "197852",
                                         :author-name      "Patrick Radden Keefe",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1538163619p5/197852.jpg\n",
                                         :title            "Say Nothing: A True Story of Murder and Memory in Northern Ireland",
                                         :country          "The United States",
                                         :avg-rating       5}
                                        {:rating           "5",
                                         :author-link      "https://www.goodreads.com/author/show/15401886.Samin_Nosrat",
                                         :author-id        "15401886",
                                         :author-name      "Samin Nosrat",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1505316013p5/15401886.jpg\n",
                                         :title            "Salt, Fat, Acid, Heat: Mastering the Elements of Good Cooking",
                                         :country          "",
                                         :avg-rating       5}
                                        {:rating           "5",
                                         :author-link      "https://www.goodreads.com/author/show/1285555.Jos_Saramago",
                                         :author-id        "1285555",
                                         :author-name      "José Saramago",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1497455560p5/1285555.jpg\n",
                                         :title            "Ensaio Sobre a Cegueira",
                                         :country          "Portugal",
                                         :avg-rating       5}
                                        {:rating           "5",
                                         :author-link      "https://www.goodreads.com/author/show/1429989.Richard_P_Feynman",
                                         :author-id        "1429989",
                                         :author-name      "Richard P. Feynman",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1498916887p5/1429989.jpg\n",
                                         :title            "Surely You're Joking, Mr. Feynman!: Adventures of a Curious Character",
                                         :country          "The United States",
                                         :avg-rating       5}
                                        {:rating           "5",
                                         :author-link      "https://www.goodreads.com/author/show/45396.Robert_L_Heilbroner",
                                         :author-id        "45396",
                                         :author-name      "Robert L. Heilbroner",
                                         :author-image_url "\nhttps://images.gr-assets.com/authors/1270842051p5/45396.jpg\n",
                                         :title            "The Worldly Philosophers",
                                         :country          "The United States",
                                         :avg-rating       5}),
                          :country    {"Colombia"           "Gabriel García Márquez",
                                       "The United States"  '("Colson Whitehead"
                                                               "Jodi Picoult"
                                                               "Gillian Flynn"
                                                               "Robert L. Heilbroner"
                                                               "Richard P. Feynman"
                                                               "William Gibson"
                                                               "Patrick Radden Keefe"),
                                       "The United Kingdom" "Richard Ayoade",
                                       "Mexico"             "Laura Esquivel",
                                       "Chile"              "Roberto Bolaño",
                                       "Portugal"           "José Saramago",
                                       "Ukraine"            "Stanisław Lem",
                                       "Japan"              "Haruki Murakami"}},
           :genre-stats {:all {:name "Genres",
                               :children '({:name "True Crime", :size 1}
                                          {:name "Literary Fiction", :size 8}
                                          {:name "Cultural", :size 10}
                                          {:name "Biography", :size 5}
                                          {:name "Portugal", :size 1}
                                          {:name "Politics", :size 2}
                                          {:name "Polish Literature", :size 1}
                                          {:name "Self Help", :size 2}
                                          {:name "Fantasy", :size 6}
                                          {:name "Contemporary", :size 7}
                                          {:name "Ireland", :size 1}
                                          {:name "Health", :size 1}
                                          {:name "Memoir", :size 2}
                                          {:name "Historical Fiction", :size 5}
                                          {:name "Adult Fiction", :size 3}
                                          {:name "Food", :size 2}
                                          {:name "Novella", :size 1}
                                          {:name "Dystopia", :size 2}
                                          {:name "Physics", :size 1}
                                          {:name "European Literature", :size 8}
                                          {:name "Book Club", :size 1}
                                          {:name "Comedy", :size 1}
                                          {:name "Asia", :size 1}
                                          {:name "Fiction", :size 13}
                                          {:name "Personal Finance", :size 2}
                                          {:name "Food and Drink", :size 6}
                                          {:name "Portuguese Literature", :size 1}
                                          {:name "Cyberpunk", :size 1}
                                          {:name "Latin American Literature", :size 1}
                                          {:name "Science", :size 3}
                                          {:name "Autobiography", :size 4}
                                          {:name "Business", :size 2}
                                          {:name "Adult", :size 6}
                                          {:name "Biography Memoir", :size 1}
                                          {:name "Science Fiction", :size 8}
                                          {:name "Japan", :size 1}
                                          {:name "Race", :size 3}
                                          {:name "History", :size 3}
                                          {:name "Speculative Fiction", :size 2}
                                          {:name "Mystery", :size 7}
                                          {:name "Magical Realism", :size 4}
                                          {:name "Economics", :size 4}
                                          {:name "Thriller", :size 2}
                                          {:name "African American", :size 2}
                                          {:name "Realistic Fiction", :size 1}
                                          {:name "Classics", :size 6}
                                          {:name "Cookbooks", :size 1}
                                          {:name "Culinary", :size 1}
                                          {:name "Horror", :size 1}
                                          {:name "Literature", :size 7}
                                          {:name "Japanese Literature", :size 1}
                                          {:name "Latin American", :size 3}
                                          {:name "Social Science", :size 1}
                                          {:name "Culture", :size 1}
                                          {:name "Philosophy", :size 2}
                                          {:name "Humor", :size 3}
                                          {:name "Science Fiction Fantasy", :size 2}
                                          {:name "Suspense", :size 1}
                                          {:name "Irish Literature", :size 1}
                                          {:name "Romance", :size 2}
                                          {:name "Crime", :size 4}
                                          {:name "Drama", :size 1}
                                          {:name "Film", :size 1}
                                          {:name "Money", :size 1}
                                          {:name "Poland", :size 1}
                                          {:name "Cooking", :size 1}
                                          {:name "Novels", :size 11}
                                          {:name "Asian Literature", :size 1}
                                          {:name "Mystery Thriller", :size 1}
                                          {:name "Currency", :size 1}
                                          {:name "Nonfiction", :size 6}
                                          {:name "Audiobook", :size 10}
                                          {:name "Personal Development", :size 1}
                                          {:name "British Literature", :size 1}
                                          {:name "Money Management", :size 1}
                                          {:name "Spanish Literature", :size 4}
                                          {:name "Reference", :size 2}
                                          {:name "Finance", :size 3}
                                          {:name "Historical", :size 8})}}})

(defn all-books
  [data]
  [:div {:class "vh-100 w-100 bg-black pa3"}
   [:div {:class "fl w-100 h-25"}
    [:h1 {:class "f-headline tracked-tight lh-solid b v-top white"}
     "In 2020 you've read all of these books"]]
   (map (fn [book]
          [:img {:src   (:book-cover book)
                 :width "8%"
                 :class "pa3"}])

        (get-in data [:book-stats :all]))])

(defn longest-books
  [data]
  (let [books (get-in data [:book-stats :top-5-longest])
        largest (first (map #(edn/read-string (:num_pages %1)) books))]
    [:div {:class "vh-100 bg-green pa3 flex items-center"}
     [:div {:class "fl w-40 h-75"}
      [:h1 {:class "f-headline tracked-tight lh-solid b v-top black"}
       "The largest books you've read."]
      [:div {:class "h-25 w-100 flex items-center black"}
       [:h1 {:class "f3 lh-solid tr georgia normal black"}
        "Now those are some big boys!"]]]
     [:div {:class "fl w-60 h-100 pa flex items-center"}
      [ResponsiveContainer {:width "100%" :height "75%"}
       [BarChart {:width 800 :height 500 :data books}
        [XAxis {:stroke "black" :dataKey "title"}]
        [YAxis {:stroke "black " :scale "linear" :type "number" :domain #js [0, largest]}]
        [CartesianGrid {:stroke "black" :strokeDasharray '(3 7)}]
        [Legend]
        [Tooltip]
        [Bar {:name "Number of Pages" :dataKey "num_pages" :fill "#00000"}]]]]]))


(defn shortest-books
  [data]
  [:div {:class "vh-100 bg-navy pa3 flex items-center"}
   [:div {:class "fl w-60 h-100 pa flex items-center"}
    [ResponsiveContainer {:width "100%" :height "75%"}
     [PieChart {:width 800 :height 500}
      [Pie {:data    (get-in data [:book-stats :bottom-5-longest]) :dataKey "num_pages"
            :nameKey "title" :outerRadius "90%" :innerRadius "30%" :fill "#19A974"
            :label (fn [entry] (str (. entry -name) ", " (. entry -value) " pages"))
            :stroke "#001B44"}]
      ]]]
   [:div {:class "fl w-40 h-75"}
    [:h1 {:class "f-headline tracked-tight lh-solid b v-top green tr"}
     "The shortest books you've read."]
    [:div {:class "h-25 w-100 green"}
     [:h1 {:class "f3 lh-solid tr georgia normal green"}
      "A couple of appetizers."]]]])


(defn books-by-month
  [data]
  [:div {:class "vh-100 bg-washed-red pa3"}
   [:div {:class "h-75 w-75 pt5 center"}
    [ResponsiveContainer {:width "100%" :height "75%"}
     [AreaChart {:width 800 :height 300 :data (get-in data [:book-stats :by-month])}
      [XAxis {:stroke "#001B44" :dataKey "month"}]
      [YAxis {:stroke "#001B44"}]
      [CartesianGrid {:stroke "#001B44" :strokeDasharray '(3 7)}]
      [Tooltip]
      [Legend]
      [Area {:type "monotone" :name "Books Read (Cumulative)" :fill "#001B44" :stroke "#001B44"  :fillOpacity 1 :dataKey "sum"}]
      [Area {:type "monotone" :name "Books Read (Absolute)" :fill "#357EDD" :stroke "#357EDD"  :fillOpacity 1 :dataKey "count"}]
      ]]]
   [:div
    [:h1 {:class "f-headline tracked-tight lh-solid b v-top navy tr"} "Your breakdown by month"]]])

(defn books-by-read-count
  [data]
  (let [data (get-in data [:book-stats :by-read-time])
        max (max (map :count data))]
    [:div {:class "vh-100 bg-blue pa3 center"}
     [:div
      [:h1 {:class "f-headline tracked-tight lh-solid b v-top washed-red tl"} "Your reading speed breakdown"]]
     [:div {:class "h-75 w-100 pt5 center"}
      [ResponsiveContainer {:width "100%" :height "150%" }
       [RadialBarChart {:width       "100%" :height "10%" :data data
                        :outerRadius "100%" :innerRadius "10%" :startAngle 180 :endAngle 0}
        [PolarAngleAxis {:tick false :type "number" :domain #js [0 max] :dataKey "count" :angleAxisId 0}]
        [RadialBar {:dataKey "count" :minAngle 5 :label #js {:fill "#FFDFDF"} :background #js {:fill "#FFDFDF"} :angleAxisId 0}]
        [Legend {:verticalAlign "middle"
                 :wrapperStyle #js {:paddingTop "5%"}
                 :formatter     (fn [value entry index]
                                  (reagent/as-element [:span {:style {:color "#FFDFDF"}} (. entry -value)]))
                 }]]]]]))

(defn fastest-books
  [data]
  [:div {:class "vh-100 bg-washed-green pa3 flex items-center"}
   [:div {:class "fl w-40 h-75"}
    [:h1 {:class "f-headline tracked-tight lh-solid b v-top purple"}
     "You've read these the fastest."]]
   [:div {:class "fl w-60 h-100 pa flex items-center"}
    [ResponsiveContainer {:width "100%" :height "75%"}
     [BarChart {:width 800 :height 300 :data (get-in data [:book-stats :top-5-fastest])}
      [XAxis {:stroke "#5E2CA5" :dataKey "title"}]
      [YAxis {:stroke "#5E2CA5"}]
      [CartesianGrid {:stroke "#5E2CA5" :strokeDasharray '(3 7)}]
      [Tooltip]
      [Legend]
      [Bar {:dataKey "read-time-days" :fill "#5E2CA5" :name "Time to read (days)"}]]]]])

(defn slowest-books
  [data]
  [:div {:class "vh-100 bg-purple pa3 flex items-center"}
   [:div {:class "fl w-60 h-100 pa flex items-center"}
    [ResponsiveContainer {:width "100%" :height "75%"}
     [PieChart {:width 800 :height 500}
      [Pie {:data    (get-in data [:book-stats :bottom-5-fastest]) :dataKey "read-time-days"
            :nameKey "title" :outerRadius "90%" :innerRadius "30%" :fill "#CDECFF"
            :label (fn [entry] (str (. entry -name) ", " (. entry -value) " days"))
            :stroke  "#5E2CA5"}]
      ]]]
   [:div {:class "fl w-40 h-75"}
    [:h1 {:class "f-headline tracked-tight lh-solid b v-top lightest-blue tr"}
     "Your slowest reads"]
    [:div {:class "h-25 w-100 lightest-blue"}
     [:h1 {:class "f3 lh-solid tr georgia normal lightest-blue"}
      "How many pages left??"]]]])

(defn planning-v-discovered
  [data]
  (let [planning (get-in data [:book-stats :planning])
        discovered (get-in data [:book-stats :discovered-this-year])
        all (list planning discovered)]
    [:div {:class "vh-100 bg-gold pa3 flex items-center"}
     [:div {:class "fl w-60 h-100 pa flex items-center"}
      [ResponsiveContainer {:width "100%" :height "75%"}
       [PieChart {:width 800 :height 500}
        [Pie {:data    all :dataKey "count"
              :nameKey "name" :outerRadius "90%" :innerRadius "30%" :fill "#5E2CA5"
              :label   (fn [entry] (str (. entry -name) ", " (. entry -value) " books"))
              :stroke  "#FFB700"}]
        ]]]
     [:div {:class "fl w-40 h-75"}
      [:h1 {:class "f-headline tracked-tight lh-solid b v-top purple tr"}
       "Are you spontaneous or a planner?"]
      [:div {:class "h-25 w-100 purple"}
       [:h1 {:class "f3 lh-solid tr georgia normal purple"}
        "Can't stop buying new books, huh?"]]]]))

(defn genres-map
  [data]
  (let [data (list (get-in data [:genre-stats :all]))]
    [:div {:class "vh-100 bg-navy pa3 flex items-center"}
     [:div {:class "fl w-60 h-100 pa flex items-center"}
      [ResponsiveContainer {:width "100%" :height "75%"}
       [Treemap {:data data :width 800 :height 500
                 :dataKey "size" :fill "#FFB700" :stroke "#001B44"
                 :isAnimationActive true :animationDuration 1000 :animationEasing "linear"}]]]
     [:div {:class "fl w-40 h-75"}
      [:h1 {:class "f-headline tracked-tight lh-solid b v-top gold tr"}
       "Are you spontaneous or a planner?"]
      [:div {:class "h-25 w-100 gold"}
       [:h1 {:class "f3 lh-solid tr georgia normal gold"}
        "Can't stop buying new books, huh?"]]]]))

(defn statistics-component []
  (fn [] [:div {:class " bg-black"}

          (all-books data)
          (longest-books data)
          (shortest-books data)
          (books-by-month data)
          (books-by-read-count data)
          (fastest-books data)
          (slowest-books data)
          (planning-v-discovered data)
          (genres-map data)]))








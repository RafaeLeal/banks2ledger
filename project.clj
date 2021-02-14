(defproject banks2ledger "1.2.2"
  :description "Banks' CSV to ledger converter with probabilistic payment matching"
  :url "https://tomszilagyi.github.io/payment-matching"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/data.csv "0.1.4"]
                 [org.clojure/tools.cli "0.4.2"]]
  :main ^:skip-aot banks2ledger.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})

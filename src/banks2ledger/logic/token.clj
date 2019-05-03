(ns banks2ledger.logic.token
  (:require clojure.string))

(defn tokenize
  "Create tokens from string
  One string may become one or more tokens, returned as a seq
    - Convert to uppercase
    - replace dates with degraded forms
    - Split at '/' ',' and space"
  [str]
  (->>
    (-> str
        clojure.string/upper-case
        (clojure.string/replace #"20\d{6}" "YYYYMMDD")
        (clojure.string/replace #"/\d{2}-\d{2}-\d{2}" "/YY-MM-DD")
        (clojure.string/split #",|/| "))
    (filter (complement clojure.string/blank?))))


(defn accounts
  "Receives a list of trasactions and returns a set of accounts"
  [ledger]
  (->> (mapcat :transaction/entries ledger)
       (map :entry/account)
       set))

(defn account-transactions
  "Get all transactions that envolves a given account"
  [acc ledger]
  (filter (fn [{:transaction/keys [entries]}]
            (some #(= % acc) (map :entry/account entries))) ledger))

(defn accounts-tokens [ledger]
  (->> (accounts ledger)
       (map (fn [acc] {acc (->> (account-transactions acc ledger)
                                (map :transaction/description)
                                (mapcat tokenize))}))
       (into {})))

(defn accounts-token-stats [ledger]
  (->> (map (fn [[acc tokens]] [acc (->> (group-by identity tokens)
                                         (map #(do [(first %) (count (second %))]))
                                         (into {}))]) (accounts-tokens ledger))
       (into {})))


(comment
  (def txs [#:transaction{:date "2019/02/22",
                          :description "Bar do Mica",
                          :entries '(#:entry{:account "Liabilities:NuCreditCard", :value -10M, :currency "R$"}
                                     #:entry{:account "Expenses:Food:Snacks", :value 10M, :currency "R$"})}
            #:transaction{:date "2019/02/22",
                          :description "Bar do Mica",
                          :entries '(#:entry{:account "Liabilities:NuCreditCard", :value -10M, :currency "R$"}
                                      #:entry{:account "Expenses:Food:Snacks", :value 10M, :currency "R$"})}
            #:transaction{:date "2019/02/22",
                          :description "Desconto Antecipação Samsung A7 Seguro",
                          :entries '(#:entry{:account "Equity:Nubank:Antecipation", :value -8.49M, :currency "R$"}
                                     #:entry{:account "Liabilities:NuCreditCard", :value 8.49M, :currency "R$"})}
            #:transaction{:date "2019/02/22",
                          :description "Samsung A7 3/12",
                          :entries '(#:entry{:account "Liabilities:NuCreditCard", :value -183.33M, :currency "R$"}
                                     #:entry{:account "Expenses:Gift:Myself", :value 183.33M, :currency "R$"})}])
  (accounts txs)
  (account-transactions "Liabilities:NuCreditCard" txs)
  (accounts-tokens txs)
  (accounts-token-stats txs))

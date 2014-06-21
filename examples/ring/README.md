# Ring in a Bubble

Start the app with

    lein run

and reload the code in the bubble with the lein-bubble plugin:

    lein bubble blow localhost 9090 bubble-ring.main/*bubble* \
        src/bubble_ring/log.clj src/bubble_ring/core.clj

Currently, lein-bubble doesn't support `:before` and `:after` callbacks.
In order to pass them you have to call `bubble.core/blow` on your own:

```
lein repl :connect localhost:9090 <<EOF
(bubble.core/blow
  bubble-ring.main/*bubble*
  '[[$(cat src/bubble_ring/log.clj)]
    [$(cat src/bubble_ring/core.clj)]]
  :before (fn [lookup]
            ((lookup 'bubble-ring.core/start!)))
  :after (fn [lookup]
           ((lookup 'bubble-ring.core/stop!))))
EOF
```

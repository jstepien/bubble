# Ring in a Bubble

Start the app with

    lein run

and reload the code in the bubble with

```
lein repl :connect 0:9090 <<EOF
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

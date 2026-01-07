#!/bin/bash

/bin/ollama serve &

pid=$!

sleep 5

echo "🔴 Retrieving Llama 3.2 model..."
ollama pull llama3.2

echo "🟢 Model ready! Starting server..."
wait $pid
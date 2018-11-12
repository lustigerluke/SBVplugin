#!/bin/bash

mkdir ntestData

for filename in testData/*.txt; do
    dd bs=1 count=8000 if="$filename" of="n$filename"

done

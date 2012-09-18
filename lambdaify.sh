#!/bin/bash
for filename in 'F.java' 'F2.java'
do
    sed 's/public final \([^{]*\){/public \1default {/' `find . -name $filename` | sed 's/abstract class/interface/' > tmp.txt
    cp tmp.txt `find . -name $filename`
done

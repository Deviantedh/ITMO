#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 <config: 1|2|3>" >&2
  exit 1
fi

CONFIG="$1"
BASE="/Users/devianted/IdeaProjects/TPO4"
JMETER="/Users/devianted/Downloads/apache-jmeter-5.6.3/bin/jmeter"
PLAN="$BASE/lab4-single-config-assertion.jmx"
REPORT_DIR="$BASE/jmeter_html_reports_assertion_separate"

mkdir -p "$REPORT_DIR"

jtl="$BASE/load-assertion-config${CONFIG}.jtl"
log="$BASE/load-assertion-config${CONFIG}.log"
out="$REPORT_DIR/config${CONFIG}"

echo "=== Running config=${CONFIG} ==="
rm -f "$jtl" "$log"
rm -rf "$out"

"$JMETER" \
  -n \
  -t "$PLAN" \
  -Jconfig="$CONFIG" \
  -Jusers=12 \
  -Jthroughput=240 \
  -Jrampup=60 \
  -Jduration=180 \
  -l "$jtl" \
  -j "$log"

echo "=== Generating HTML report for config=${CONFIG} ==="
"$JMETER" -g "$jtl" -o "$out"

echo "JTL: $jtl"
echo "Report: $out/index.html"

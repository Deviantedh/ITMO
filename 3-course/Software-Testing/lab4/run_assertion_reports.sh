#!/usr/bin/env bash
set -euo pipefail

BASE="/Users/devianted/IdeaProjects/TPO4"
JMETER="/Users/devianted/Downloads/apache-jmeter-5.6.3/bin/jmeter"
PLAN="$BASE/lab4-single-config-assertion.jmx"
REPORT_DIR="$BASE/jmeter_html_reports_assertion_separate"

mkdir -p "$REPORT_DIR"

for config in 1 2 3; do
  jtl="$BASE/load-assertion-config${config}.jtl"
  log="$BASE/load-assertion-config${config}.log"
  out="$REPORT_DIR/config${config}"

  echo "=== Running config=${config} ==="
  rm -f "$jtl" "$log"
  rm -rf "$out"

  "$JMETER" \
    -n \
    -t "$PLAN" \
    -Jconfig="$config" \
    -Jusers=12 \
    -Jthroughput=240 \
    -Jrampup=60 \
    -Jduration=180 \
    -l "$jtl" \
    -j "$log"

  echo "=== Generating HTML report for config=${config} ==="
  "$JMETER" -g "$jtl" -o "$out"

  echo "Report: $out/index.html"

  if [[ "$config" != "3" ]]; then
    echo "Cooldown 60s..."
    sleep 60
  fi
done

echo "Done. Reports are in: $REPORT_DIR"

Regex to find broken links
- End of the line
\$\(l:[^\)]*\)(?:(?!\$\(\/l\)).)*$
- Multiple opening without closing 
\$\(l:[^\)]*\)(?:(?!\$\(\/l\)).)*$
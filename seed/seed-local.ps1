# MongoDB local. PathFinder folder එකේ සිට run කරන්න:  .\seed\seed-local.ps1
$import = "C:\Program Files\MongoDB\Tools\100\bin\mongoimport.exe"
$files  = "C:\Program Files\MongoDB\Tools\100\bin\mongofiles.exe"
$cols = @(
  @('user_db','users'), @('user_db','student_profiles'), @('user_db','email_dispatches'), @('user_db','saved_posts'),
  @('notice_db','job_categories'), @('notice_db','notices'), @('notice_db','notice_views'),
  @('flyer_db','flyers')
)
foreach ($c in $cols) {
  $db = $c[0]; $col = $c[1]
  Write-Host "Loading $db.$col"
  & $import --host localhost --port 27017 --db $db --collection $col --drop --jsonArray --file ".\seed\$db\$col.json"
}
Get-ChildItem .\seed\flyers\*.png | ForEach-Object {
  & $files --host localhost --port 27017 --db flyer_db delete $_.Name 2>$null | Out-Null
  & $files --host localhost --port 27017 --db flyer_db --local $_.FullName --type image/png put $_.Name
}
Write-Host 'Dummy data loaded.'
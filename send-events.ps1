# Send 5 test events to PORT 8080 (not 8081!)
for ($i = 1; $i -le 5; $i++) {
    $body = @{
        userId = "user_$i"
        action = "page_view"
    } | ConvertTo-Json

    Write-Host "Sending event $i..."
    
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/activity" `
          -Method POST `
          -ContentType "application/json" `
          -Body $body `
          -TimeoutSec 5
        
        Write-Host "Success: $($response.Content)" -ForegroundColor Green
    } catch {
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    Start-Sleep -Milliseconds 500
}

Write-Host "Done!"
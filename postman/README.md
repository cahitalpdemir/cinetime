# CineTime Postman Collection

## Dosyalar

- `CineTime Backend Hardening Tests.postman_collection.json`: Full backend hardening regression collection.
- `CineTime Local.postman_environment.json`: Local environment dosyasi Postman'den export edilerek takimla paylasilabilir.

## Import

Postman:

```text
Import -> Files -> CineTime Backend Hardening Tests.postman_collection.json
```

Environment:

```text
CineTime Local
```

Secili degiskenler:

```text
baseUrl=http://localhost:8081
adminEmail=admin@cinetime.com
adminPassword=ChangeMe123!
customerEmail=customer@cinetime.com
customerPassword=Customer123
```

## Calistirma Sirasi

Collection klasorleri sirayla calistirilmalidir:

```text
00 Health
01 Auth
02 Movie
03 Cinema Hall Seat
04 Showtime
05 Booking Payment Ticket
06 Negative Tests
```

## Notlar

- Temiz database ile ilk calistirmada create requestleri en sorunsuz sonucu verir.
- Daha once ayni film/sinema/customer olusturulduysa create endpointleri duplicate hatasi verebilir.
- Bu durumda mevcut ID'ler environment'a yazilip sonraki requestlerden devam edilebilir.
- Admin istekleri `adminToken`, customer istekleri `customerToken` kullanir.
- `adminPassword` degeri yerel `.env` dosyasindaki `ADMIN_PASSWORD` ile ayni olmalidir.

## Newman

Backend calisirken collection komut satirindan da kosulabilir:

```bash
newman run "CineTime Backend Hardening Tests.postman_collection.json" -e "CineTime Local.postman_environment.json"
```

Last clean-database verification on 2026-07-04:

```text
32 requests, 0 failures
29 assertions, 0 failures
```

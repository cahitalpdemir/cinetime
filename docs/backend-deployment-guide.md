# CineTime Backend Deployment Guide

Bu dokuman CineTime backend'i Render uzerinde canli ortama almak icin hazirlandi.
Frontend ve backend ayri deploy edilecek; backend once ayaga kalkacak, frontend daha sonra backend URL'ini kullanacak.

## 1. Hedef mimari

```text
Kullanici -> Frontend -> Backend API -> Render Postgres
                                  |
                                  -> Render Key Value / Redis uyumlu token store
```

Minimum canli mimari:

- Backend: Render Web Service, Docker runtime
- Database: Render Postgres
- Refresh token store: Render Key Value, Redis/Valkey uyumlu
- Frontend: Ayri deploy edilen Next.js uygulamasi
- CORS: Backend sadece frontend domaininden gelen browser isteklerine izin verir

## 2. Render Blueprint

Repo kok dizininde `render.yaml` bulunur. Render Dashboard uzerinden Blueprint olarak bu repo secildiginde su kaynaklar olusur:

- `cinetime-backend`: Docker tabanli backend web service
- `cinetime-db`: PostgreSQL database
- `cinetime-redis`: Redis uyumlu Render Key Value servisi

Health check path:

```text
/api/movies
```

Not: Production security aktifken Render health check token gonderemez. Bu nedenle health check, public ve DB baglantisini da yoklayan `GET /api/movies` endpointi uzerinden yapilir.

Render Blueprint icinde otomatik uretilen veya baglanan degerler:

| Degisken | Kaynak |
| --- | --- |
| `DB_URL` | Render Postgres internal connection string |
| `DB_USERNAME` | Render Postgres user |
| `DB_PASSWORD` | Render Postgres password |
| `REDIS_URL` | Render Key Value internal connection string |
| `JWT_SECRET` | Render tarafinda otomatik uretilir |
| `TICKET_QR_SECRET` | Render tarafinda otomatik uretilir |

Ilk Blueprint kurulumunda elle girilecek degerler:

| Degisken | Ornek |
| --- | --- |
| `ADMIN_EMAIL` | `admin@cinetime.com` |
| `ADMIN_PASSWORD` | guclu bir admin sifresi |
| `FRONTEND_URL` | frontend canli domaini, gecici olarak Render frontend URL'i |
| `CORS_ALLOWED_ORIGINS` | frontend origin'i, ornek: `https://cinetime-frontend.onrender.com` |

Not: Frontend henuz canli degilse `FRONTEND_URL` ve `CORS_ALLOWED_ORIGINS` gecici olarak `http://localhost:3000` veya daha sonra guncellenecek placeholder domain ile girilebilir. Frontend deploy edilince bu iki deger Render Dashboard'da guncellenmelidir.

## 3. Render'da adim adim kurulum

1. Backend branch'ini GitHub'a pushla ve PR'i merge et.
2. Render Dashboard'a gir.
3. `New` -> `Blueprint` sec.
4. Backend repository'sini sec.
5. Render `render.yaml` dosyasini okuyacak.
6. `sync: false` olan alanlari doldur:
   - `ADMIN_EMAIL`
   - `ADMIN_PASSWORD`
   - `FRONTEND_URL`
   - `CORS_ALLOWED_ORIGINS`
7. Blueprint'i uygula.
8. Database ve Key Value servislerinin hazir olmasini bekle.
9. Backend deploy loglarini takip et.
10. Health check sonucunu kontrol et:

```text
https://cinetime-backend.onrender.com/api/movies
```

Beklenen cevap:

```json
{
  "status": "UP"
}
```

## 4. Ortam degiskenleri

Production profilinde uygulama su kritik degiskenleri kullanir:

| Degisken | Aciklama |
| --- | --- |
| `SPRING_PROFILES_ACTIVE=prod` | Production profilini acar |
| `DB_URL` veya `DATABASE_URL` | PostgreSQL connection string |
| `DB_USERNAME` | PostgreSQL kullanici adi |
| `DB_PASSWORD` | PostgreSQL sifresi |
| `REFRESH_TOKEN_STORE=redis` | Refresh tokenlar Redis uyumlu store'da tutulur |
| `REDIS_URL` | Render Key Value internal URL |
| `JWT_SECRET` | JWT imzalama secret degeri |
| `TICKET_QR_SECRET` | Ticket QR imzalama secret degeri |
| `ADMIN_EMAIL` | Ilk admin kullanicisi |
| `ADMIN_PASSWORD` | Ilk admin sifresi |
| `FRONTEND_URL` | Reset password linkleri icin frontend domaini |
| `CORS_ALLOWED_ORIGINS` | Browser isteklerine izin verilen frontend origin |

Render Postgres connection string'i `postgresql://...` formatinda gelir. Uygulama bunu startup aninda JDBC formatina cevirir. Bu nedenle Render'da ekstra JDBC duzenlemesi yapmaya gerek yoktur.

## 5. Redis / Render Key Value

Production icin onerilen deger:

```env
REFRESH_TOKEN_STORE=redis
REDIS_URL=redis://...
```

Tek instance demo veya gecici staging icin Redis olmadan calismak mumkundur:

```env
REFRESH_TOKEN_STORE=memory
```

`memory` secilirse refresh tokenlar uygulama restart edildiginde silinir. Cok instance production icin uygun degildir.

## 6. Swagger ve API docs

Production'da Swagger default olarak kapali tutulur:

```env
SWAGGER_UI_ENABLED=false
API_DOCS_ENABLED=false
```

Staging/demo icin gerekirse acilabilir:

```env
SWAGGER_UI_ENABLED=true
API_DOCS_ENABLED=true
```

Swagger URL:

```text
/swagger-ui.html
```

## 7. Docker local kontrol

Local Docker build:

```bash
docker build -t cinetime-backend .
```

Local Docker run:

```bash
docker run --env-file .env -p 8081:8081 cinetime-backend
```

## 8. Deploy sonrasi smoke test

Canli backend ayaga kalktiktan sonra sirayla kontrol et:

1. `GET /api/movies` -> `200 OK`
2. `POST /auth/login` admin token aliyor mu?
3. `GET /api/movies` public calisiyor mu?
4. `GET /cinemas` public calisiyor mu?
5. `GET /showtimes` public calisiyor mu?
6. Admin token ile `POST /api/movies` calisiyor mu?
7. Customer register/login calisiyor mu?
8. Booking -> payment -> tickets akisi tamamlanabiliyor mu?
9. Token olmadan `/admin/**` endpointleri kapali mi?
10. Frontend canli domaininden CORS hatasi olmadan API cagrisi yapilabiliyor mu?

## 9. Frontend baglantisi

Frontend tarafinda backend URL'i su degere yazilir:

```env
NEXT_PUBLIC_API_URL=https://cinetime-backend.onrender.com
```

Backend tarafinda frontend domaini su iki degere yazilir:

```env
FRONTEND_URL=https://cinetime-frontend.onrender.com
CORS_ALLOWED_ORIGINS=https://cinetime-frontend.onrender.com
```

Frontend custom domain alirsa bu iki backend degiskeni yeniden guncellenmelidir.

## 10. Rollback plani

Bir deploy sorun cikarirsa:

- Once Render'da son calisan deploy'a rollback yap.
- Sadece config hatasi varsa kod rollback etmek yerine environment variable'lari duzelt.
- Database migration sorunu varsa Flyway history tablosunu ve migration dosyalarini kontrol etmeden elle tablo degistirme.
- Redis kaynakli sorun varsa gecici demo icin `REFRESH_TOKEN_STORE=memory` ile servis ayaga kaldirilabilir; production icin kalici cozum Redis baglantisini duzeltmektir.

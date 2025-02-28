FROM golang:1.23.6-alpine AS builder

WORKDIR /app

COPY go.mod go.sum ./

RUN go mod download

COPY . .

RUN ls -l

RUN go build -o main .

FROM alpine:latest

RUN apk --no-cache add ca-certificates

COPY --from=builder /app/main /app/main
COPY .env .env

EXPOSE 8080
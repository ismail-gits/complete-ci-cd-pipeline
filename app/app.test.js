const request = require('supertest');
const express = require('express');
const path = require('path');
const fs = require('fs');

let app;

beforeAll(() => {
  app = express();

  app.get('/', function (req, res) {
    res.sendFile(path.join(__dirname, "index.html"));
  });

  app.get('/profile-picture-1', function (req, res) {
    let img = fs.readFileSync(path.join(__dirname, "images/profile-1.jpg"));
    res.writeHead(200, {'Content-Type': 'image/jpg' });
    res.end(img, 'binary');
  });

  app.get('/profile-picture-2', function (req, res) {
    let img = fs.readFileSync(path.join(__dirname, "images/profile-2.jpg"));
    res.writeHead(200, {'Content-Type': 'image/jpg' });
    res.end(img, 'binary');
  });
});

describe('GET /', () => {
  it('should return index.html', async () => {
    const res = await request(app).get('/');
    expect(res.statusCode).toBe(200);
    expect(res.header['content-type']).toBe('text/html; charset=UTF-8');
  });
});

describe('GET /profile-picture-1', () => {
  it('should return profile-1.jpg', async () => {
    const res = await request(app).get('/profile-picture-1');
    expect(res.statusCode).toBe(200);
    expect(res.header['content-type']).toBe('image/jpg');
    expect(Buffer.isBuffer(res.body)).toBe(true);
  });
});

describe('GET /profile-picture-2', () => {
  it('should return profile-2.jpg', async () => {
    const res = await request(app).get('/profile-picture-2');
    expect(res.statusCode).toBe(200);
    expect(res.header['content-type']).toBe('image/jpg');
    expect(Buffer.isBuffer(res.body)).toBe(true);
  });
});

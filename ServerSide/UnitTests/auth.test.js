const app = require("../server");
const request = require("supertest");
const mongoose = require("mongoose");
const User = require("../Models/user_model");

const email = "test@test.com";
const pwd = "123456";
const name = "test name";
const phone = "0501234567";

beforeAll((done) => {
  //   User.remove({ email: email }, (err) => {
  //     done();
  //   });
  User.deleteOne({ email: email }, (err) => {
    done();
  });
});

afterAll((done) => {
  //   User.remove({ email: email }, (err) => {
  //     mongoose.connection.close();
  //     done();
  //   });
  User.deleteOne({ email: email }, (err) => {
    mongoose.connection.close();
    done();
  });
});

describe("Testing Auth API", () => {
  test("Authentication test - registration", async () => {
    const response = await request(app).post("/auth/register").send({
      email: email,
      password: pwd,
      name: name,
      phone: phone,
    });
    expect(response.statusCode).toEqual(200);
  });

  test("Authentication test - login", async () => {
    const response = await request(app).post("/auth/login").send({
      email: email,
      password: pwd,
    });
    expect(response.statusCode).toEqual(200);
  });
});

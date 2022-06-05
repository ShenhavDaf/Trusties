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
  let accessToken = "";
  let userID = "";

  test("Authentication test - registration", async () => {
    const response = await request(app).post("/auth/register").send({
      email: email,
      password: pwd,
      name: name,
      phone: phone,
    });
    expect(response.statusCode).toEqual(200);
    userID = response.body.newUser._id;
  });

  /* ******************************************** */
  /* ******************************************** */

  test("Authentication test - login", async () => {
    const response = await request(app).post("/auth/login").send({
      email: email,
      password: pwd,
    });
    expect(response.statusCode).toEqual(200);
    accessToken = response.body.accessToken;
  });

  /* ******************************************** */
  /* ******************************************** */

  test("Authentication test - getCurrUser", async () => {
    const response = await request(app).get("/auth/getCurrUser");
    expect(response.statusCode).toEqual(200);
    expect(response.body.id).toEqual(userID);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("Authentication test - resend email", async () => {
    const response = await request(app).post("/auth/resendEmail");
    expect(response.statusCode).toEqual(200);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("Authentication test - get all users", async () => {
    const response = await request(app).get("/auth/allUsers");
    expect(response.statusCode).toEqual(200);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("Authentication test - find user by email", async () => {
    const response = await request(app)
      .get("/auth/findByEmail")
      .query({ emailAddress: email });
    expect(response.statusCode).toEqual(200);
    expect(response.body._id).toEqual(userID);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("Authentication test - find user by ID", async () => {
    const response = await request(app)
      .get("/auth/findById")
      .query({ id: userID });
    expect(response.statusCode).toEqual(200);
    expect(response.body.name).toEqual(name);
  });

  /* ******************************************** */
  /* ******************************************** */

  // test("Authentication test - refresh token", async () => {
  //   const response = await request(app).get("/auth/refreshToken");
  //   expect(response.statusCode).toEqual(200);
  // });

  /* ******************************************** */
  /* ******************************************** */

  // test("Authentication test - logout", async () => {
  //   const response = await request(app).get("/auth/logout");
  //   expect(response.statusCode).toEqual(200);
  // });
});

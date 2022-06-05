const app = require("../server");
const request = require("supertest");
const mongoose = require("mongoose");
const User = require("../Models/user_model");
const Post = require("../Models/post_model");

const email = "test@test.com";
const pwd = "123456";
const name = "test username (from unittest)";
const phone = "test phone (from unittest)";

beforeAll((done) => {
  User.deleteOne({ email: email }, (err) => {
    done();
  });
});

afterAll((done) => {
  User.deleteOne({ email: email }, (err) => {
    mongoose.connection.close();
    done();
  });
});

describe("Testing Post API", () => {
  const postTitle = "test post title (from unittest)";
  const postMessage = "this is my test post (from unittest)";
  const postCategory = "Car";
  const sender = "Adi";
  const postPhoto =
    "https://play-lh.googleusercontent.com/V_P-I-UENK93ahkQgOWel8X8yFxjhOOfMAZjxXrqp311Gm_RBtlDXHLQhwFZN8n4aIQ";
  let accessToken = "";
  let userId = "";
  let postID;
  let volunteerID = "";

  test("sos test - registration", async () => {
    const response = await request(app).post("/auth/register").send({
      email: email,
      password: pwd,
      name: name,
      phone: phone,
    });
    expect(response.statusCode).toEqual(200);
    userId = response.body.newUser._id;
  });

  /* ******************************************** */
  /* ******************************************** */

  test("sos test - login", async () => {
    const response = await request(app).post("/auth/login").send({
      email: email,
      password: pwd,
    });
    expect(response.statusCode).toEqual(200);
    accessToken = response.body.accessToken;
  });

  // /* ******************************************** */
  // /* ******************************************** */

  test("sos test - add new sos post", async () => {
    const response = await request(app)
      .post("/sos/add")
      .set({ authorization: "JWT " + accessToken })
      .send({
        email: email,
        title: postTitle,
        description: postMessage,
        category: postCategory,
        circle: 2,
      });

    expect(response.statusCode).toEqual(200);
    postID = response.body._id;

    const newSos = (
      await request(app)
        .get("/post/" + postID)
        .set({ authorization: "JWT " + accessToken })
    ).body;

    expect(newSos.description).toEqual(postMessage);
    // expect(newPost.sender).toEqual(sender);
  });

  /* ******************************************** */
  /* ******************************************** */
});

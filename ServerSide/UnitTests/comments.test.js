const app = require("../server");
const request = require("supertest");
const mongoose = require("mongoose");
const User = require("../Models/user_model");

const email = "ortallik@gmail.com";
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

describe("Testing Comment API", () => {
  const postDescription = "post message (from comments test)";
  const postTitle = "post title (from comments test)";
  const commentMessage = "comment text (from comment test)";
  const postCategory = "Car";
  let accessToken = "";
  let userId = "";
  let postID, commentID;

  test("Comment test - registration", async () => {
    const response = await request(app).post("/auth/register").send({
      email: email,
      password: pwd,
      name: name,
      phone: phone,
    });
    expect(response.statusCode).toEqual(200);
    userId = response.body._id;
  });

  /* ******************************************** */
  /* ******************************************** */

  test("Comment test - login", async () => {
    const response = await request(app).post("/auth/login").send({
      email: email,
      password: pwd,
    });
    expect(response.statusCode).toEqual(200);
    accessToken = response.body.accessToken;
  });

  /* ******************************************** */
  /* ******************************************** */

  test("Comment test - add new post", async () => {
    const response = await request(app)
      .post("/post/add")
      .set({ authorization: "JWT " + accessToken })
      .send({
        email: email,
        title: postTitle,
        description: postDescription,
        category: postCategory,
      });
    expect(response.statusCode).toEqual(200);
    postID = response.body.post._id;
  });

  /* ******************************************** */
  /* ******************************************** */

  test("Comment test - add new comment", async () => {
    const response = await request(app)
      .post("/comment/add")
      .set({ authorization: "JWT " + accessToken })
      .send({
        sender: email,
        postId: postID,
        content: commentMessage, //message
      });
    expect(response.statusCode).toEqual(200);
    commentID = response.body.comment._id;
  });

  /* ******************************************** */
  /* ******************************************** */

  test("Comment test - get all comments (by post id)", async () => {
    const response = await request(app)
      .get("/comment/" + postID + "/allComments")
      .set({ authorization: "JWT " + accessToken });
    expect(response.statusCode).toEqual(200);
    const comment = response.body[0];
    expect(comment.message).toEqual(commentMessage);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("Comment test - get comment by id", async () => {
    const response = await request(app)
      .get("/comment/" + commentID)
      .set({ authorization: "JWT " + accessToken });
    expect(response.statusCode).toEqual(200);
    const comment = response.body;
    expect(comment.message).toEqual(commentMessage);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("Comment test - edit comment", async () => {
    const response = await request(app)
      .post("/comment/edit/" + commentID)
      .set({ authorization: "JWT " + accessToken })
      .send({
        content: "after edit - " + commentMessage,
      });
    expect(response.statusCode).toEqual(200);
    const updateComment = response.body.comment;
    expect(updateComment.message).toEqual("after edit - " + commentMessage);
  });

  /* ******************************************** */
  /* ******************************************** */

  test("Comment test - delete comment", async () => {
    const response = await request(app)
      .post("/comment/delete/" + commentID)
      .set({ authorization: "JWT " + accessToken })
      .send();
    expect(response.statusCode).toEqual(200);
    const deletedComment = response.body.comment;
    expect(deletedComment.isDeleted).toEqual(true);
  });
});

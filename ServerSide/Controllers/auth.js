const User = require('../Models/user_model');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const sendEmail = require('../Utils/sendEmail');

const sendError = (res, code, msg) => {
  return res.status(code).send({
    status: 'fail',
    error: msg,
  });
};

function getRandomInt(min, max) {
  min = Math.ceil(min);
  max = Math.floor(max);
  return Math.floor(Math.random() * (max - min) + min);
}

var randomCode = 0;
var email;
const register = async (req, res, next) => {
  email = req.body.email;
  const password = req.body.password;
  //Hen's Addition
  const name = req.body.name;
  // const verifyCode = req.body.verifyCode;

  try {
    const exists = await User.findOne({ email: email });

    if (exists != null) return sendError(res, 400, 'user exist');

    const salt = await bcrypt.genSalt(10);
    const hashPwd = await bcrypt.hash(password, salt);

    const user = User({
      email: email,
      password: hashPwd,
      name: name,
    });

    randomCode = getRandomInt(10000, 99999);
    await sendEmail(user.email, 'Verify Email', String(randomCode));

    newUser = await user.save();
    res.status(200).send({ newUser, randomCode });
  } catch (err) {
    res.status(400).send({
      status: 'fail',
      error: err.message,
    });
  }
};

const login = async (req, res, next) => {
  const email = req.body.email;
  const password = req.body.password;

  if (email == null || password == null)
    return sendError(res, 400, 'Wrong email or password');

  try {
    const user = await User.findOne({ email: email });
    if (user == null) return sendError(res, 400, 'Wrong email or password');

    const match = await bcrypt.compare(password, user.password);
    if (!match) return sendError(res, 400, 'Wrong email or password');

    const accessToken = await jwt.sign(
      { id: user._id },
      process.env.ACCESS_TOKEN_SECRET,
      { expiresIn: process.env.JWT_TOKEN_EXPIRATION }
    );

    res.status(200).send({ accessToken: accessToken });
  } catch (err) {
    return sendError(res, 400, err.message);
  }
};

const logout = async (req, res, next) => {
  res.status(400).send({
    status: 'fail',
    error: 'not implemented',
  });
};

const resendEmail = async (req, res, next) => {
  await sendEmail(email, 'Verify Email', String(randomCode));
};

// const verifyEmail = async (req, res, next) => {
//   try {
//     const user = await User.findOne({ email: "ortallik@gmail.com" });
//     if (!user) return res.status(400).send({ message: "Invalid link1" });

//     const token = await Token.findOne({
//       userId: user._id,
//       // token: req.params.token,
//     });
//     if (!token) return res.status(400).send({ message: "Invalid link2" });

//     // await User.updateOne({ _id: user._id, verified: true });
//     // await token.remove();

//     res.status(200).send({ message: "Email verified successfully" });
//   } catch (error) {
//     console.log("inside");
//     res.status(500).send({ message: "Internal Server Error" });
//   }
// };

// const verifyRandomCode = async (req, res, next) => {
//   var verify = randomCode;

//   //TODO!!!!!- maybe to save a the generated code in mongo( with crypto)
//   // and then the client side will read the data ( with GET )
// };

module.exports = {
  login,
  register,
  logout,
  resendEmail,
  // verifyEmail,
  // verifyRandomCode,
};

/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
    "./public/index.html"
  ],
  theme: {
    extend: {
      fontFamily: {
        'barlow': ['Barlow Condensed', 'sans-serif'],
        'caveat': ['Caveat', 'cursive'],
      },
      colors: {
        'custom-green': '#737d62',
        'custom-green-hover': '#7A7F75',
      }
    },
  },
  plugins: [],
}
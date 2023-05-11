# This is a sample Python script.
import matplotlib.pyplot as plt
import json
# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import pandas as pd
import json
from pandas import json_normalize
import plotly.express as px
def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    print_hi('PyCharm')

    dictionary = json.load(open('file8_1.json', 'r'))

    df2 = pd.DataFrame.from_dict(dictionary, orient="index")
    print(df2)

    fig = px.scatter(df2)

    fig.update_traces(marker=dict(size=12,
                                  line=dict(width=2,
                                            color='DarkSlateGrey')),
                      selector=dict(mode='markers'))

    fig.update_layout(legend=dict(title_font_family="Times New Roman",
                                  font=dict(size=20)
                                  ))

    fig.update_xaxes(tickfont=dict(size=22, family='Times New Roman'))
    fig.update_yaxes(tickfont=dict(size=22, family='Times New Roman'))

    fig.show()

# See PyCharm help at https://www.jetbrains.com/help/pycharm/

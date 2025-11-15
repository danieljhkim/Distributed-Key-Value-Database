package cmd

import (
	"fmt"
	"strings"

	"github.com/spf13/cobra"
)

var setCmd = &cobra.Command{
	Use:   "set [key] [value]",
	Short: "Set key to hold string value",
	Args:  cobra.MinimumNArgs(2),
	Run: func(cmd *cobra.Command, args []string) {
		key := args[0]
		value := strings.Join(args[1:], " ")
		response, err := kvClient.ExecuteCommand(fmt.Sprintf("KV SET %s %s", key, value))
		if err != nil {
			fmt.Printf("Error: %v\n", err)
			return
		}
		fmt.Println(response)
	},
}

func init() {
	rootCmd.AddCommand(setCmd)
}
